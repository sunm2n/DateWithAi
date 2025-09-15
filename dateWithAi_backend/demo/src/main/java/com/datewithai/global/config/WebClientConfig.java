package com.datewithai.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

@Configuration
public class WebClientConfig {
    
    @Value("${ai.python-service.base-url}")
    private String pythonServiceBaseUrl;
    
    @Bean
    public WebClient pythonServiceWebClient() {
        ConnectionProvider connectionProvider = ConnectionProvider.builder("python-service")
                .maxConnections(100)
                .maxIdleTime(Duration.ofSeconds(20))
                .maxLifeTime(Duration.ofSeconds(60))
                .pendingAcquireTimeout(Duration.ofSeconds(60))
                .evictInBackground(Duration.ofSeconds(120))
                .build();
        
        HttpClient httpClient = HttpClient.create(connectionProvider)
                .responseTimeout(Duration.ofSeconds(60));
        
        return WebClient.builder()
                .baseUrl(pythonServiceBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
    
    @Bean
    public WebClient defaultWebClient() {
        return WebClient.builder()
                .build();
    }
}