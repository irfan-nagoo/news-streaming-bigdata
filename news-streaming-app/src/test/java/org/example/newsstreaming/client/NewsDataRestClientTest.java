package org.example.newsstreaming.client;

import org.example.newsstreaming.response.NewsDataResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NewsDataRestClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private NewsDataRestClient newsDataRestClient;


    @Test
    @SuppressWarnings("unchecked")
    void getLatestNews_Success() {
        NewsDataResponse resp = new NewsDataResponse();
        resp.setStatus(HttpStatus.OK.name());
        ResponseEntity<NewsDataResponse> entity = new ResponseEntity<>(resp, HttpStatus.OK);
        newsDataRestClient.setUrl("http://wwww.junit.com");
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class),
                any(Class.class), any(Map.class))).thenReturn(entity);

        NewsDataResponse response = newsDataRestClient.getLatestNews("nextPageTest");
        assertEquals(HttpStatus.OK.name(), response.getStatus());
    }

    @Test
    @SuppressWarnings("unchecked")
    void getLatestNews_Exception() {
        HttpStatusCodeException statusCodeException = mock(HttpStatusCodeException.class);
        when(statusCodeException.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        newsDataRestClient.setUrl("http://wwww.junit.com");
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class),
                any(Class.class), any(Map.class))).thenThrow(statusCodeException);
        // BAD Request
        IllegalArgumentException responseExp = assertThrows(IllegalArgumentException.class,
                () -> newsDataRestClient.getLatestNews("nextPageTest"));
        assertEquals("Invalid request parameters", responseExp.getMessage());

        // UNAUTHORIZED
        when(statusCodeException.getStatusCode()).thenReturn(HttpStatus.FORBIDDEN);
        IllegalArgumentException responseExp2 = assertThrows(IllegalArgumentException.class,
                () -> newsDataRestClient.getLatestNews("nextPageTest"));
        assertEquals("The apiKey or Authorization parameters are Invalid", responseExp2.getMessage());

        // INTERNAL_SERVER_ERROR
        when(statusCodeException.getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
        IllegalArgumentException responseExp3 = assertThrows(IllegalArgumentException.class,
                () -> newsDataRestClient.getLatestNews("nextPageTest"));
        assertEquals("Processing error [500 INTERNAL_SERVER_ERROR] occurred on the destination side",
                responseExp3.getMessage());

    }
}