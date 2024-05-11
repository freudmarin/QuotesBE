package com.marin.quotesdashboardbackend.adapter.impl;

import com.marin.quotesdashboardbackend.adapter.QuoteAdapter;
import com.marin.quotesdashboardbackend.dtos.QuoteDTO;
import com.marin.quotesdashboardbackend.dtos.QuotePageDTO;
import com.marin.quotesdashboardbackend.dtos.SearchQuoteResponseDTO;
import com.marin.quotesdashboardbackend.entities.Quote;
import com.marin.quotesdashboardbackend.repositories.AuthorRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class QuoteAdapterImpl implements QuoteAdapter {

    private final AuthorRepository authorRepository;

    public QuoteAdapterImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public Quote fromQuoteDTO(QuoteDTO quoteDTO) {
        Quote entity = new Quote();
        entity.setText(quoteDTO.getContent());
        entity.setAuthor(authorRepository.findAuthorByName(quoteDTO.getAuthor()));
        return entity;
    }

    @Override
    public List<Quote> fromQuotePageDTO(QuotePageDTO quotePageDTO) {
        return quotePageDTO.getQuotes().stream()
                .map(this::fromQuoteDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<Quote> fromSearchQuotesResponseDTO(SearchQuoteResponseDTO searchQuoteResponseDTO) {
        return searchQuoteResponseDTO.getResults().stream()
                .map(this::fromQuoteDTO)
                .collect(Collectors.toList());
    }
}
