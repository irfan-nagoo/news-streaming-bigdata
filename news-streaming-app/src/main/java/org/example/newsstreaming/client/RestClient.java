package org.example.newsstreaming.client;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.util.Map;

/**
 * An interface for all rest clients
 *
 * @author irfan.nagoo
 */
public interface RestClient<I, O> {

    O invoke(HttpMethod method, String uri, HttpHeaders requestHeaders, I request,
             Map<String, String> uriParameters, Class<O> responseClass);
}
