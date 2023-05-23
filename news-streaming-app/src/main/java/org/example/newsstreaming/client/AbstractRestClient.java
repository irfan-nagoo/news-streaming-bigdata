package org.example.newsstreaming.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.example.newsstreaming.constants.MessagingConstants.*;

/**
 * Default implementation for the RestClient
 *
 * @author irfan.nagoo
 */

@Slf4j
public abstract class AbstractRestClient<I, O> implements RestClient<I, O> {

    private final RestTemplate restTemplate;

    protected AbstractRestClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public O invoke(HttpMethod httpMethod, String uri, HttpHeaders requestHeaders, I request,
                    Map<String, String> uriParameters, Class<O> responseClass) {
        HttpEntity<I> requestEntity = new HttpEntity<>(request, requestHeaders);
        try {
            ResponseEntity<O> responseEntity = restTemplate.exchange(uri, httpMethod, requestEntity,
                    responseClass, uriParameters);
            log.info("Received Http Response code [{}]", responseEntity.getStatusCode());
            return responseEntity.getBody();
        } catch (RestClientException rce) {
            log.error("Error occurred while invoking rest endpoint", rce);
            if (rce instanceof HttpStatusCodeException) {
                HttpStatusCodeException sce = (HttpStatusCodeException) rce;
                switch (sce.getStatusCode()) {
                    case BAD_REQUEST:
                    case NOT_FOUND:
                        throw new IllegalArgumentException(INVALID_REQUEST_ERROR_MSG);
                    case FORBIDDEN:
                    case UNAUTHORIZED:
                        throw new IllegalArgumentException(INVALID_API_KEY_ERROR_MSG);
                    case INTERNAL_SERVER_ERROR:
                    default:
                        throw new IllegalArgumentException(String.format(REST_PROCESSING_ERROR_MSG,
                                sce.getStatusCode()));
                }
            } else if (rce instanceof ResourceAccessException) {
                throw rce;
            } else {
                throw new RuntimeException(String.format(REST_PROCESSING_ERROR_MSG, "UNKNOWN"));
            }
        }
    }

}
