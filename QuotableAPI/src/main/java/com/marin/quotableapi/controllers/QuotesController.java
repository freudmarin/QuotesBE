package com.marin.quotableapi.controllers;

import com.marin.quotableapi.models.SearchQuotesResponse;
import com.marin.quotableapi.models.Quote;
import com.marin.quotableapi.models.QuotePage;
import com.marin.quotableapi.services.QuoteService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/quotes")
public class QuotesController {
    private final QuoteService quoteService;

    public QuotesController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @GetMapping()
    public Flux<QuotePage> getQuotes(
            @RequestParam(value = "maxLength", required = false) Integer maxLength,
            @RequestParam(value = "minLength", required = false) Integer minLength,
            @RequestParam(value = "tags", required = false) String tags,
            @RequestParam(value = "author", required = false) String author,
            @RequestParam(value = "sortBy", required = false, defaultValue = "dateAdded") String sortBy,
            @RequestParam(value = "order", required = false) String order,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value = "page", defaultValue = "1") int page
    ) {
        return quoteService.getQuotes(maxLength, minLength, tags, author, sortBy, order, limit, page);
    }

    @GetMapping("random")
    public Flux<Quote> getRandomQuotes(
            @RequestParam(value = "limit", required = false, defaultValue = "1") Integer limit,
            @RequestParam(value = "maxLength", required = false) Integer maxLength,
            @RequestParam(value = "minLength", required = false) Integer minLength,
            @RequestParam(value = "tags", required = false) String tags,
            @RequestParam(value = "author", required = false) String author
    ) {
        return quoteService.getRandomQuotes(limit, maxLength, minLength, tags, author);
    }

    @GetMapping("search")
    public Mono<SearchQuotesResponse> searchQuotes(
            @RequestParam String query,
            @RequestParam(required = false, defaultValue = "content,author") String fields,
            @RequestParam(required = false, defaultValue = "0") int fuzzyMaxEdits,
            @RequestParam(required = false, defaultValue = "20") int limit,
            @RequestParam(required = false, defaultValue = "1") int skip) {
        return quoteService.searchQuotes(query, fields, fuzzyMaxEdits, limit, skip);
    }
}
