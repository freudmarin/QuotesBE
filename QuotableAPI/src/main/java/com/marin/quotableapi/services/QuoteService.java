package com.marin.quotableapi.services;

import com.marin.quotableapi.models.Quote;
import com.marin.quotableapi.models.SearchQuotesResponse;
import com.marin.quotableapi.models.QuotePage;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class QuoteService {

    private final WebClient webClient;

    public QuoteService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Flux<QuotePage> getQuotes(
            Integer maxLength,
            Integer minLength,
            String tags,
            String author,
            String sortBy,
            String order,
            int limit,
            int page
    ) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/quotes")
                        .queryParamIfPresent("maxLength", Optional.ofNullable(maxLength))
                        .queryParamIfPresent("minLength", Optional.ofNullable(minLength))
                        .queryParamIfPresent("tags", Optional.ofNullable(tags))
                        .queryParamIfPresent("author", Optional.ofNullable(author))
                        .queryParamIfPresent("sortBy", Optional.ofNullable(sortBy))
                        .queryParamIfPresent("order", Optional.ofNullable(order))
                        .queryParam("limit", limit)
                        .queryParam("page", page)
                        .build())
                .retrieve()
                .bodyToFlux(QuotePage.class);
    }


    public Flux<Quote> getRandomQuotes(
            Integer limit,
            Integer maxLength,
            Integer minLength,
            String tags,
            String author
    ) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/quotes/random")
                        .queryParamIfPresent("limit", Optional.ofNullable(limit))
                        .queryParamIfPresent("maxLength", Optional.ofNullable(maxLength))
                        .queryParamIfPresent("minLength", Optional.ofNullable(minLength))
                        .queryParamIfPresent("tags", Optional.ofNullable(tags))
                        .queryParamIfPresent("author", Optional.ofNullable(author))
                        .build())
                .retrieve()
                .bodyToFlux(Quote.class);
    }

    public Mono<SearchQuotesResponse> searchQuotes(
            String query, String fields,
            int fuzzyMaxEdits, int limit, int skip) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/quotes")
                        .queryParam("query", query)
                        .queryParamIfPresent("fields", Optional.ofNullable(fields))
                        .queryParam("fuzzyMaxEdits", Optional.ofNullable(fuzzyMaxEdits))
                        .queryParam("limit", Optional.of(limit))
                        .queryParam("skip", Optional.of(skip))
                        .build())
                .retrieve()
                .bodyToMono(SearchQuotesResponse.class);
    }
}
