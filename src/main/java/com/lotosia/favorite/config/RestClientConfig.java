package com.lotosia.favorite.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

/**
 * @author: nijataghayev
 */

@Configuration
public class RestClientConfig {

    @Value("${catalog.service.url:http://localhost:8080}")
    private String catalogServiceUrl;

    @Value("${catalog.service.connect-timeout-ms:3000}")
    private int connectTimeout;

    @Value("${catalog.service.read-timeout-ms:5000}")
    private int readTimeout;

    @Bean
    public RestClient catalogRestClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofMillis(connectTimeout));
        requestFactory.setReadTimeout(Duration.ofMillis(readTimeout));

        return RestClient.builder()
                .baseUrl(catalogServiceUrl)
                .requestFactory(requestFactory)
                .build();
    }
}
