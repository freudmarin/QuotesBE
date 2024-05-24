package com.marin.quotableapi.services;

import com.marin.quotableapi.enums.Order;
import com.marin.quotableapi.enums.SortBy;
import com.marin.quotableapi.models.Author;
import com.marin.quotableapi.models.AuthorPage;
import com.marin.quotableapi.models.SearchAuthorsResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class AuthorService {

    private final WebClient webClient;

    public AuthorService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Mono<AuthorPage> listAuthors(int page, Integer limit, Order order, SortBy sortBy,
                                        String slug) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/authors")
                        .queryParamIfPresent("sortBy", Optional.ofNullable(sortBy))
                        .queryParamIfPresent("order", Optional.ofNullable(order))
                        .queryParamIfPresent("slug", Optional.ofNullable(slug))
                        .queryParamIfPresent("page", Optional.ofNullable(page))
                        .queryParamIfPresent("limit", Optional.ofNullable(limit))
                        .build())
                .retrieve()
                .bodyToMono(AuthorPage.class);
    }

    public Mono<SearchAuthorsResponse> searchAuthors(String query, boolean autocomplete,
                                                     int matchThreshold, int limit, int page) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/authors")
                        .queryParam("query", query)
                        .queryParam("autocomplete", autocomplete)
                        .queryParam("matchThreshold", matchThreshold)
                        .queryParam("limit", limit)
                        .queryParam("page", page)
                        .build())
                .retrieve()
                .bodyToMono(SearchAuthorsResponse.class);
    }

    public Mono<Author> getAuthorById(String id) {
        return webClient.get()
                .uri("/authors/{id}", id)
                .retrieve()
                .bodyToMono(Author.class);
    }

    public Mono<Author> getAuthorBySlug(String slug) {
        return webClient.get()
                .uri("/authors/slug/{slug}", slug)
                .retrieve()
                .bodyToMono(Author.class);
    }
}
