package org.example.newsstreaming.service;

import org.example.newsstreaming.constants.SentimentType;
import org.example.newsstreaming.response.NewsSentimentReportResponse;
import org.example.newsstreaming.response.NewsSentimentResponse;

import java.time.LocalDate;

/**
 * @author irfan.nagoo
 */
public interface NewsSentimentService {

    NewsSentimentResponse getNewsSentiments(LocalDate startDate, LocalDate endDate, int pageSize, String nextPage);

    NewsSentimentResponse searchByTitle(String title, int pageSize, String nextPage);

    NewsSentimentResponse getNewsSentimentsBySentiment(SentimentType sentimentType, int pageSize, String nextPage);

    NewsSentimentReportResponse getNewsSentimentReport(LocalDate startDate, LocalDate endDate);

    void deleteByPartitionDate(LocalDate partDate);

}
