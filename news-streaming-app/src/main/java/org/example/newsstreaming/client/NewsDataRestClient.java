package org.example.newsstreaming.client;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.newsstreaming.response.NewsDataResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author irfan.nagoo
 */

@Component
@Setter
@Slf4j
public class NewsDataRestClient extends AbstractRestClient<Void, NewsDataResponse> {

    @Value("${org.example.news.streaming.newsdata.url}")
    private String url;
    @Value("${org.example.news.streaming.newsdata.apiKey}")
    private String apiKey;
    @Value("${org.example.news.streaming.newsdata.query}")
    private String query;
    @Value("${org.example.news.streaming.newsdata.category}")
    private String category;
    @Value("${org.example.news.streaming.newsdata.country}")
    private String country;
    @Value("${org.example.news.streaming.newsdata.language}")
    private String language;


    public NewsDataRestClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    /**
     * Retrieves the latest headlines from NewData.io as per the given parameters. This
     * method is retry able as per the given configuration.
     *
     * @param nextPage Next page parameter to retrieve next page.
     * @return NewsData response with headlines
     */
    @Retryable(include = ResourceAccessException.class, maxAttemptsExpression = "${org.example.news.streaming.retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${org.example.news.streaming.retry.backOffDelay}"))
    public NewsDataResponse getLatestNews(String nextPage) {
        log.info("Invoking NewsData rest endpoint");
        Map<String, String> uriParameterValues = getUriParameterValues();
        if (StringUtils.hasText(nextPage)) {
            uriParameterValues.put("nextPage", nextPage);
        }
        return invoke(HttpMethod.GET, buildUrlWithParameters(nextPage), getHttpHeaders(), null,
                uriParameterValues, NewsDataResponse.class);
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.put(HttpHeaders.CONTENT_TYPE, Collections.singletonList(MediaType.APPLICATION_JSON_VALUE));
        return httpHeaders;
    }

    private String buildUrlWithParameters(String nextPage) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(url + "/news");
        if (StringUtils.hasText(apiKey)) {
            uriComponentsBuilder.queryParam("apiKey", "{apiKey}");
        }
        if (StringUtils.hasText(query)) {
            uriComponentsBuilder.queryParam("q", "{query}");
        }
        if (StringUtils.hasText(category)) {
            uriComponentsBuilder.queryParam("category", "{category}");
        }
        if (StringUtils.hasText(country)) {
            uriComponentsBuilder.queryParam("country", "{country}");
        }
        if (StringUtils.hasText(language)) {
            uriComponentsBuilder.queryParam("language", "{language}");
        }
        if (StringUtils.hasText(nextPage)) {
            uriComponentsBuilder.queryParam("page", "{nextPage}");
        }
        return uriComponentsBuilder.encode()
                .toUriString();
    }

    private Map<String, String> getUriParameterValues() {
        Map<String, String> uriParameterMap = new HashMap<>();
        if (StringUtils.hasText(apiKey)) {
            uriParameterMap.put("apiKey", apiKey);
        }
        if (StringUtils.hasText(query)) {
            uriParameterMap.put("query", query);
        }
        if (StringUtils.hasText(category)) {
            uriParameterMap.put("category", category);
        }
        if (StringUtils.hasText(country)) {
            uriParameterMap.put("country", country);
        }
        if (StringUtils.hasText(language)) {
            uriParameterMap.put("language", language);
        }
        return uriParameterMap;
    }
}
