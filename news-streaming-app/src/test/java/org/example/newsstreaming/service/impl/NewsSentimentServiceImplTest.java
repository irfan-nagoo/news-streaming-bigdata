package org.example.newsstreaming.service.impl;

import org.example.newsstreaming.constants.SentimentType;
import org.example.newsstreaming.entity.NewsSentiment;
import org.example.newsstreaming.entity.NewsSentimentPK;
import org.example.newsstreaming.exception.RecordNotFoundException;
import org.example.newsstreaming.repository.NewsSentimentRepository;
import org.example.newsstreaming.response.NewsSentimentReportResponse;
import org.example.newsstreaming.response.NewsSentimentResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NewsSentimentServiceImplTest {

    @Mock
    private NewsSentimentRepository sentimentRepository;

    @InjectMocks
    private NewsSentimentServiceImpl sentimentService;


    @Test
    void getNewsSentiments_Success() {
        NewsSentiment newsSentiment = new NewsSentiment();
        newsSentiment.setTitle("Test Title");
        NewsSentimentPK key = new NewsSentimentPK();
        key.setPubDate(LocalDateTime.now().minusDays(1));
        newsSentiment.setKey(key);
        when(sentimentRepository.findByKeyPartDateIn(anyList(), any()))
                .thenReturn(new SliceImpl<>(Collections.singletonList(newsSentiment)));

        NewsSentimentResponse response = sentimentService.getNewsSentiments(LocalDate.now().minusDays(1), LocalDate.now(),
                10, null);
        assertEquals(1, response.getNewsSentiments().size());
        assertEquals("Test Title", response.getNewsSentiments().get(0).getTitle());
    }

    @Test
    void getNewsSentiments_NotFound() {
        when(sentimentRepository.findByKeyPartDateIn(anyList(), any()))
                .thenReturn(new SliceImpl<>(Collections.emptyList()));

        RecordNotFoundException exception = assertThrows(RecordNotFoundException.class,
                () -> sentimentService.getNewsSentiments(LocalDate.now().minusDays(1), LocalDate.now(),
                        10, null));
        assertEquals("The News Sentiment data not found", exception.getMessage());
    }

    @Test
    void searchByTitle() {
        NewsSentiment newsSentiment = new NewsSentiment();
        newsSentiment.setTitle("Test Title");
        NewsSentimentPK key = new NewsSentimentPK();
        key.setPubDate(LocalDateTime.now().minusDays(1));
        newsSentiment.setKey(key);
        when(sentimentRepository.findByTitleContaining(any(), any()))
                .thenReturn(new SliceImpl<>(Collections.singletonList(newsSentiment)));

        NewsSentimentResponse response = sentimentService.searchByTitle("any search string", 10, null);
        assertEquals(1, response.getNewsSentiments().size());
        assertEquals("Test Title", response.getNewsSentiments().get(0).getTitle());
    }

    @Test
    void getNewsSentimentsBySentiment() {
        NewsSentiment newsSentiment = new NewsSentiment();
        newsSentiment.setTitle("Test Title");
        NewsSentimentPK key = new NewsSentimentPK();
        key.setPubDate(LocalDateTime.now().minusDays(1));
        newsSentiment.setKey(key);
        when(sentimentRepository.findBySentiment(anyString(), any()))
                .thenReturn(new SliceImpl<>(Collections.singletonList(newsSentiment)));

        NewsSentimentResponse response = sentimentService.getNewsSentimentsBySentiment(SentimentType.POSITIVE,
                10, null);
        assertEquals(1, response.getNewsSentiments().size());
        assertEquals("Test Title", response.getNewsSentiments().get(0).getTitle());
    }

    @Test
    void getNewsSentimentReport_Positive() {
        NewsSentiment newsSentiment = new NewsSentiment();
        newsSentiment.setTitle("Test Title");
        newsSentiment.setSentiment("Positive");
        NewsSentimentPK key = new NewsSentimentPK();
        key.setPubDate(LocalDateTime.now().minusDays(1));
        newsSentiment.setKey(key);
        when(sentimentRepository.findByKeyPartDateIn(anyList()))
                .thenReturn(Collections.singletonList(newsSentiment));

        NewsSentimentReportResponse response = sentimentService.getNewsSentimentReport(LocalDate.now().minusDays(1),
                LocalDate.now());
        assertEquals("100.00%", response.getPositiveSentiments());
        assertEquals("Positive", response.getPeriodOverallSentiment().getValue());
    }

    @Test
    void getNewsSentimentReport_Neutral() {
        NewsSentiment newsSentiment1 = new NewsSentiment();
        newsSentiment1.setTitle("Test Title1");
        newsSentiment1.setSentiment("Positive");
        NewsSentiment newsSentiment2 = new NewsSentiment();
        newsSentiment2.setTitle("Test Title2");
        newsSentiment2.setSentiment("Negative");
        when(sentimentRepository.findByKeyPartDateIn(anyList()))
                .thenReturn(Arrays.asList(newsSentiment1, newsSentiment2));

        NewsSentimentReportResponse response = sentimentService.getNewsSentimentReport(LocalDate.now().minusDays(1),
                LocalDate.now());
        assertEquals("50.00%", response.getPositiveSentiments());
        assertEquals("50.00%", response.getNegativeSentiments());
        assertEquals("Neutral", response.getPeriodOverallSentiment().getValue());
    }

    @Test
    void deleteByPartitionDate() {
        doNothing().when(sentimentRepository).deleteByKeyPartDate(any());
        assertDoesNotThrow(() -> sentimentService.deleteByPartitionDate(LocalDate.now()));
    }
}