package org.example.newsstreaming.schedule;

import org.example.newsstreaming.client.NewsDataRestClient;
import org.example.newsstreaming.domain.NewsObject;
import org.example.newsstreaming.domain.NewsSentimentObject;
import org.example.newsstreaming.producer.AsyncMessageProducer;
import org.example.newsstreaming.response.NewsDataResponse;
import org.example.newsstreaming.response.NewsSentimentResponse;
import org.example.newsstreaming.service.NewsSchedulerAuditService;
import org.example.newsstreaming.service.NewsSentimentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewsDataRetrieverTaskTest {

    @Mock
    private NewsDataRestClient newsDataRestClient;

    @Mock
    private NewsSentimentService newsSentimentService;

    @Mock
    private AsyncMessageProducer<NewsObject> kafkaProducer;

    @Mock
    private NewsSchedulerAuditService auditService;

    @InjectMocks
    private NewsDataRetrieverTask retrieverTask;

    @Test
    void run() {
        NewsDataResponse newsDataResponse = new NewsDataResponse();
        newsDataResponse.setStatus("success");
        NewsObject news = new NewsObject();
        news.setPubDate(LocalDateTime.now());
        newsDataResponse.setResults(Collections.singletonList(news));
        NewsSentimentObject newsSentiment = new NewsSentimentObject();
        newsSentiment.setPubDate(LocalDateTime.now().minusDays(1));
        NewsSentimentResponse newsSentimentResponse = new NewsSentimentResponse(HttpStatus.OK.name(), "Some message");
        newsSentimentResponse.setNewsSentiments(Collections.singletonList(newsSentiment));
        retrieverTask.setNumRecords("10");
        when(newsDataRestClient.getLatestNews(any()))
                .thenReturn(newsDataResponse);
        when(newsSentimentService.getNewsSentiments(any(), any(), anyInt(), any()))
                .thenReturn(newsSentimentResponse);
        doNothing().when(kafkaProducer).sentMessage(any(NewsObject.class));
        doNothing().when(auditService).save(anyString(), any(), any(), anyString());

        assertDoesNotThrow(retrieverTask::run);
    }

}