package org.example.newsstreaming.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.client.RestTemplate;

/**
 * @author irfan.nagoo
 */

@Configuration
@PropertySources({
        @PropertySource("classpath:/application.properties"),
        @PropertySource(value = "classpath:/application-${spring.profiles.active}.properties", ignoreResourceNotFound = true)
})
@EnableRetry(proxyTargetClass = true)
public class NewsStreamingConfig {

    @Value("${org.example.news.streaming.rest.connectionTimeout}")
    private String connectionTimeout;

    @Value("${org.example.news.streaming.rest.readTimeout}")
    private String readTimeout;

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Integer.parseInt(connectionTimeout));
        requestFactory.setReadTimeout(Integer.parseInt(readTimeout));
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        return restTemplate;
    }
}
