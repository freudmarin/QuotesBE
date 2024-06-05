package com.marin.quotableapi.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
public class WebClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.build();
    }

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .baseUrl("https://api.quotable.io")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter(logRequest())
                .filter(logResponse())
                .filter(handleErrors());
    }

    private ExchangeFilterFunction logRequest() {
        return (clientRequest, next) -> {
            // Log request details
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            return next.exchange(clientRequest);
        };
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            // Log response details
            log.info("Response: {}", clientResponse.statusCode());
            return Mono.just(clientResponse);
        });
    }

    private ExchangeFilterFunction handleErrors() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().equals(HttpStatus.OK)) {
                // Handle 200 OK
                log.info("Response OK: {}", clientResponse.statusCode());
                return Mono.just(clientResponse);
            } else if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                // Handle 404 NOT FOUND
                log.error("Not Found: {}", clientResponse.statusCode());
                return Mono.error(new RuntimeException("Resource not found"));
            } else if (clientResponse.statusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
                // Handle 500 INTERNAL SERVER ERROR
                log.error("Internal Server Error: {}", clientResponse.statusCode());
                return Mono.error(new RuntimeException("Internal server error occurred"));
            } else {
                log.error("An unexpected error occurred: {}", clientResponse.statusCode());
                return Mono.just(clientResponse);
            }
        });
    }
}







