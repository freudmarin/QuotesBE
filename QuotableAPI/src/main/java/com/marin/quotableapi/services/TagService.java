package com.marin.quotableapi.services;

import com.marin.quotableapi.enums.Order;
import com.marin.quotableapi.enums.SortBy;
import com.marin.quotableapi.models.Tag;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.Optional;

@Service
public class TagService {

    private final WebClient webClient;

    public TagService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Flux<Tag> listTags(SortBy sortBy, Order sortOrder) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tags")
                        .queryParamIfPresent("sortBy", Optional.ofNullable(sortBy))
                        .queryParam("sortOrder", Optional.ofNullable(sortOrder))
                        .build())
                .retrieve()
                .bodyToFlux(Tag.class);
    }
}
