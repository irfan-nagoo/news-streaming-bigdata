package org.example.newsstreaming.service.impl;

import com.datastax.oss.protocol.internal.util.Bytes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.newsstreaming.constants.SentimentType;
import org.example.newsstreaming.entity.NewsSentiment;
import org.example.newsstreaming.exception.RecordNotFoundException;
import org.example.newsstreaming.repository.NewsSentimentRepository;
import org.example.newsstreaming.response.NewsSentimentReportResponse;
import org.example.newsstreaming.response.NewsSentimentResponse;
import org.example.newsstreaming.service.NewsSentimentService;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.example.newsstreaming.constants.MessagingConstants.*;
import static org.example.newsstreaming.constants.NewsStreamingConstants.PERCENT;

/**
 * @author irfan.nagoo
 */

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class NewsSentimentServiceImpl implements NewsSentimentService {

    private final NewsSentimentRepository newsSentimentRepository;

    @Override
    public NewsSentimentResponse getNewsSentiments(LocalDate startDate, LocalDate endDate, int pageSize, String nextPage) {
        log.info("Processing News Sentiment list request");
        validateDate(startDate, endDate);
        List<LocalDate> dateRange = getPartitionDateRange(startDate, endDate);
        PageRequest pageRequest = buildPageRequest(pageSize, nextPage);
        Slice<NewsSentiment> newsSentiments = newsSentimentRepository.findByKeyPartDateIn(dateRange, pageRequest);
        return buildPaginatedNewsSentimentResponse(newsSentiments);
    }

    @Override
    public NewsSentimentResponse searchByTitle(@NonNull String query, int pageSize, String nextPage) {
        log.info("Processing title search with query [{}]", query);
        PageRequest pageRequest = buildPageRequest(pageSize, nextPage);
        Slice<NewsSentiment> newsSentiments = newsSentimentRepository.findByTitleContaining(query, pageRequest);
        return buildPaginatedNewsSentimentResponse(newsSentiments);
    }

    @Override
    public NewsSentimentResponse getNewsSentimentsBySentiment(SentimentType sentimentType, int pageSize, String nextPage) {
        log.info("Processing News Sentiment by sentiment request");
        PageRequest pageRequest = buildPageRequest(pageSize, nextPage);
        Slice<NewsSentiment> newsSentiments = newsSentimentRepository.findBySentiment(sentimentType.getValue(), pageRequest);
        return buildPaginatedNewsSentimentResponse(newsSentiments);
    }

    @Override
    public NewsSentimentReportResponse getNewsSentimentReport(LocalDate startDate, LocalDate endDate) {
        log.info("Processing News Sentiment Report request");
        validateDate(startDate, endDate);
        List<LocalDate> dateRange = getPartitionDateRange(startDate, endDate);
        List<NewsSentiment> newsSentiments = newsSentimentRepository.findByKeyPartDateIn(dateRange);
        if (newsSentiments.isEmpty()) {
            throw new RecordNotFoundException(NOT_FOUND_MSG);
        }

        long totalRecords = newsSentiments.size();
        long positiveSentiments = newsSentiments.stream()
                .map(NewsSentiment::getSentiment)
                .filter(s -> SentimentType.POSITIVE == SentimentType.getType(s))
                .count();

        // compute positive and negative sentiments for the period
        double positivePercent = ((double) positiveSentiments / totalRecords) * 100;
        double negativePercent = ((double) (totalRecords - positiveSentiments) / totalRecords) * 100;
        NewsSentimentReportResponse response = new NewsSentimentReportResponse(HttpStatus.OK.name(),
                REQUEST_PROCESSED_MSG);
        response.setPeriodStartDate(startDate);
        response.setPeriodEndDate(endDate);
        response.setPositiveSentiments(String.format("%.02f", positivePercent) + PERCENT);
        response.setNegativeSentiments(String.format("%.02f", negativePercent) + PERCENT);
        response.setPeriodOverallSentiment(getSentiment(positivePercent, negativePercent));
        return response;
    }

    @Override
    public void deleteByPartitionDate(LocalDate partDate) {
        log.info("Deleting all News Sentiment before [{}]", partDate);
        newsSentimentRepository.deleteByKeyPartDate(partDate);
        log.info("Successfully deleted Records");
    }

    private static void validateDate(@NonNull LocalDate startDate, @NonNull LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(DATE_RANGE_ERROR_MSG);
        } else if (endDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(FUTURE_DATE_ERROR_MSG);
        }
    }

    private static NewsSentimentResponse buildPaginatedNewsSentimentResponse(Slice<NewsSentiment> newsSentiments) {
        if (!CollectionUtils.isEmpty(newsSentiments.toList())) {
            NewsSentimentResponse response = new NewsSentimentResponse(HttpStatus.OK.name(), String.format(TOTAL_RECORD_MSG,
                    newsSentiments.toList().size()));
            response.setNewsSentiments(newsSentiments.stream()
                    .map(NewsSentiment::toObject).collect(Collectors.toList()));
            setNextPage(newsSentiments, response);
            return response;
        } else {
            throw new RecordNotFoundException(NOT_FOUND_MSG);
        }
    }

    private static PageRequest buildPageRequest(int pageSize, String nextPage) {
        return StringUtils.hasText(nextPage) ? CassandraPageRequest.of(Pageable.ofSize(pageSize),
                Bytes.fromHexString(nextPage)) : CassandraPageRequest.ofSize(pageSize);
    }

    private static void setNextPage(Slice<NewsSentiment> newsSentiments, NewsSentimentResponse response) {
        CassandraPageRequest pageRequest = newsSentiments.hasNext() ? (CassandraPageRequest) newsSentiments.nextPageable()
                : null;
        if (pageRequest != null) {
            response.setNextPage(Bytes.toHexString(Objects.requireNonNull(pageRequest.getPagingState())));
        }
    }

    private static SentimentType getSentiment(double positivePercent, double negativePercent) {
        double difference = Math.abs(positivePercent - negativePercent);
        if (positivePercent > negativePercent && difference > 5) {
            return SentimentType.POSITIVE;
        } else if (negativePercent > positivePercent && difference > 5) {
            return SentimentType.NEGATIVE;
        } else {
            return SentimentType.NEUTRAL;
        }
    }

    private static List<LocalDate> getPartitionDateRange(LocalDate starDate, LocalDate endDate) {
        List<LocalDate> dateRange = new ArrayList<>();
        do {
            dateRange.add(endDate);
            endDate = endDate.minusDays(1);
        } while (endDate.isAfter(starDate));
        return dateRange;
    }
}
