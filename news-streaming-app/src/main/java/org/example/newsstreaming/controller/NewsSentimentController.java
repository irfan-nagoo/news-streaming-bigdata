package org.example.newsstreaming.controller;

import lombok.RequiredArgsConstructor;
import org.example.newsstreaming.constants.SentimentType;
import org.example.newsstreaming.response.NewsSentimentReportResponse;
import org.example.newsstreaming.response.NewsSentimentResponse;
import org.example.newsstreaming.service.NewsSentimentService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * @author irfan.nagoo
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/news-sentiment")
public class NewsSentimentController {

    private final NewsSentimentService newsSentimentService;

    /**
     * Returns the News with Sentiments in the given date range. This API supports pagination and
     * returned records are sorted by publication date.
     *
     * @param startDate Start date of the interval
     * @param endDate   End Date of the interval
     * @param pageSize  Page size
     * @param nextPage  Next page value (empty for first page). For other pages, pass the value returned from previous response
     * @return New Sentiment response
     */
    @GetMapping("/list")
    public NewsSentimentResponse getNewsSentiments(@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                   @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                   @RequestParam("pageSize") int pageSize,
                                                   @RequestParam(value = "nextPage", required = false) String nextPage) {
        return newsSentimentService.getNewsSentiments(startDate, endDate, pageSize, nextPage);
    }

    /**
     * Returns the matching New Sentiment records. This API supports pagination and
     * returned records are sorted by publication date.
     *
     * <p>
     * Note: In the backend, it uses an experimental feature of Cassandra DB which
     * is not yet recommended for prod
     *
     * @param query    Any query string we want to search on the title of the News record
     * @param pageSize Page size
     * @param nextPage Next page value (empty for first page). For other pages, pass the value returned from previous response
     * @return News Sentiment response
     */
    @GetMapping("/search")
    public NewsSentimentResponse searchByTitle(@RequestParam("q") String query,
                                               @RequestParam("pageSize") int pageSize,
                                               @RequestParam(value = "nextPage", required = false) String nextPage) {
        return newsSentimentService.searchByTitle(query, pageSize, nextPage);
    }


    /**
     * This API returns New Sentiment records by Sentiment. This API supports pagination and
     * returned records are sorted by publication date.
     *
     * @param sentiment
     * @param pageSize  Page size
     * @param nextPage  Next page value (empty for first page). For other pages, pass the value returned from previous response
     * @return News Sentiment response
     */
    @GetMapping("/{sentiment}/sentiment")
    public NewsSentimentResponse getNewsSentimentsBySentiment(@PathVariable("sentiment") String sentiment,
                                                              @RequestParam("pageSize") int pageSize,
                                                              @RequestParam(value = "nextPage", required = false) String nextPage) {
        return newsSentimentService.getNewsSentimentsBySentiment(SentimentType.getType(sentiment), pageSize, nextPage);
    }

    /**
     * This report API returns the Sentiment statistics of the given interval.
     *
     * @param startDate Start date of interval
     * @param endDate   End date of interval
     * @return News Sentiment report response
     */
    @GetMapping("/report")
    public NewsSentimentReportResponse getNewsSentimentReport(@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                              @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return newsSentimentService.getNewsSentimentReport(startDate, endDate);
    }

}
