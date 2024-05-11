package com.marin.quotesdashboardbackend.services;

import com.marin.quotesdashboardbackend.dtos.QuoteDTO;
import com.marin.quotesdashboardbackend.entities.Author;
import com.marin.quotesdashboardbackend.entities.Quote;
import com.marin.quotesdashboardbackend.repositories.AuthorRepository;
import com.marin.quotesdashboardbackend.repositories.QuoteRepository;
import com.marin.quotesdashboardbackend.retrofit.Quotes;
import com.marin.quotesdashboardbackend.retrofit.RetrofitClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class QuoteService {
    private final Quotes apiService;
    private final QuoteRepository quoteRepository;
    private final AuthorRepository authorRepository;
    private static final String serviceBaseUrl = "http://localhost:8081/api/quotes/";

    public QuoteService(QuoteRepository quoteRepository, AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
        this.apiService = RetrofitClient.getClient(serviceBaseUrl).create(Quotes.class);
        this.quoteRepository = quoteRepository;
    }

    public void syncQuotes() {
        try {
            Response<List<QuoteDTO>> response = apiService.getRandomQuotes(20).execute();
            if (response.isSuccessful()) {
                List<QuoteDTO> quotes = Optional.ofNullable(response.body()).orElse(Collections.emptyList());

                // Collect unique author names
                Set<String> authorNames = quotes.stream()
                        .map(QuoteDTO::getAuthor)
                        .collect(Collectors.toSet());

                // Fetch all authors in one go and map them by name
                Map<String, Author> authorMap = authorRepository.findAllByNameIn(authorNames)
                        .stream()
                        .collect(Collectors.toMap(Author::getName, author -> author));

                Set<String> existingTexts = quoteRepository.findTextsByContent(
                        quotes.stream().map(QuoteDTO::getContent).collect(Collectors.toList()));

                List<Quote> newQuotes = quotes.stream()
                        .filter(q -> !existingTexts.contains(q.getContent()))
                        .map(dto -> {
                            Quote quote = new Quote();
                            quote.setText(dto.getContent());
                            quote.setAuthor(authorMap.get(dto.getAuthor())); // Set author using the map
                            return quote;
                        })
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
