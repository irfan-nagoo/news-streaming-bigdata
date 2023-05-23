package org.example.newsstreaming.schedule;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.newsstreaming.client.NewsDataRestClient;
import org.example.newsstreaming.constants.ExecutionStatusType;
import org.example.newsstreaming.domain.NewsObject;
import org.example.newsstreaming.producer.AsyncMessageProducer;
import org.example.newsstreaming.response.NewsDataResponse;
import org.example.newsstreaming.response.NewsSentimentResponse;
import org.example.newsstreaming.service.NewsSchedulerAuditService;
import org.example.newsstreaming.service.NewsSentimentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.example.newsstreaming.constants.NewsStreamingConstants.NEWS_DATA_RETRIEVER_TASK;

/**
 * @author irfan.nagoo
 */

@Component
@RequiredArgsConstructor
@Setter
@Slf4j
public class NewsDataRetrieverTask implements ScheduledTask {

    @Value("${org.example.news.streaming.scheduler.numRecords}")
    private String numRecords;

    private final NewsDataRestClient newsDataRestClient;
    private final NewsSentimentService newsSentimentService;
    private final AsyncMessageProducer<NewsObject> kafkaProducer;
    private final NewsSchedulerAuditService auditService;


    @Override
    public void run() {
        log.info("Starting NewsDataRetrieverTask...");
        LocalDateTime startTimestamp = LocalDateTime.now();
        ExecutionStatusType status = ExecutionStatusType.SUCCESS;
        try {
            List<NewsObject> newsFeeds = new ArrayList<>();
            int numRequests = Integer.parseInt(numRecords) / 10;
            String nextPage = null;
            int i = 0;
            // get the latest news data from newsdata.io
            // max 10 record could be fetched in one request, iterate as per the config
            do {
                NewsDataResponse response = newsDataRestClient.getLatestNews(nextPage);
                newsFeeds.addAll(response.getResults());
                nextPage = response.getNextPage();
            } while (++i < numRequests);

            // process news feeds
            if (!CollectionUtils.isEmpty(newsFeeds)) {
                // remove duplicate records
                LocalDateTime latestPubDate = getLatestPubDate();
                if (latestPubDate != null) {
                    newsFeeds = newsFeeds.stream()
                            .filter(news -> news.getPubDate().isAfter(latestPubDate))
                            .collect(Collectors.toList());
                }

                // send feeds to kafka topic
                newsFeeds.forEach(kafkaProducer::sentMessage);
            }
        } catch (RuntimeException e) {
            log.error("Error occurred while task execution: ", e);
            status = ExecutionStatusType.ERROR;
        }
        // audit this execution
        auditService.save(NEWS_DATA_RETRIEVER_TASK, startTimestamp, LocalDateTime.now(), status.toString());
        log.info("NewsDataRetrieverTask Finished!");
    }

    private LocalDateTime getLatestPubDate() {
        LocalDateTime latestPubDate = null;
        try {
            NewsSentimentResponse response = newsSentimentService.getNewsSentiments(LocalDate.now(), LocalDate.now(),
                    1, null);
            latestPubDate = response.getNewsSentiments().get(0).getPubDate();
        } catch (RuntimeException warn) {
            log.warn("Exception while getting latest Publication date", warn);
        }
        return latestPubDate;
    }
}
