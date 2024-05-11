package com.marin.quotesdashboardbackend.services;

import com.marin.quotesdashboardbackend.adapter.QuoteAdapter;
import com.marin.quotesdashboardbackend.dtos.QuoteDTO;
import com.marin.quotesdashboardbackend.entities.Quote;
import com.marin.quotesdashboardbackend.repositories.QuoteRepository;
import com.marin.quotesdashboardbackend.retrofit.Quotes;
import com.marin.quotesdashboardbackend.retrofit.RetrofitClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class QuoteService {
    private final Quotes apiService;
    private final QuoteRepository quoteRepository;
    private final QuoteAdapter quoteAdapter;
    private static final String serviceBaseUrl = "http://localhost:8081/api/quotes/";

    public QuoteService(QuoteRepository quoteRepository, QuoteAdapter adapter) {
        this.apiService = RetrofitClient.getClient(serviceBaseUrl).create(Quotes.class);
        this.quoteRepository = quoteRepository;
        this.quoteAdapter = adapter;
    }

    public void syncQuotes() {
        try {
            Response<List<QuoteDTO>> response = apiService.getRandomQuotes(20).execute();
            if (response.isSuccessful()) {
                List<QuoteDTO> quotes = Optional.ofNullable(response.body()).orElse(Collections.emptyList());

                Set<String> existingTexts = quoteRepository.findTextsByContent(
                        quotes.stream().map(QuoteDTO::getContent).collect(Collectors.toList()));

                List<Quote> newQuotes = quotes.stream()
                        .filter(q -> !existingTexts.contains(q.getContent()))
                        .map(quoteAdapter::fromQuoteDTO)
                        .collect(Collectors.toList());
                quoteRepository.saveAll(newQuotes);
            } else {
                log.error("Failed to fetch quotes. Status: {}, Message: {}", response.code(), response.message());
            }
        } catch (IOException e) {
            log.error("Error fetching quotes: {}", e.getMessage(), e);
        }
    }
}
