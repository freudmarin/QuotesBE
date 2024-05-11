package com.marin.quotesdashboardbackend.adapter;

import com.marin.quotesdashboardbackend.dtos.QuoteDTO;
import com.marin.quotesdashboardbackend.dtos.QuotePageDTO;
import com.marin.quotesdashboardbackend.dtos.SearchQuoteResponseDTO;
import com.marin.quotesdashboardbackend.entities.Quote;

import java.util.List;

public interface QuoteAdapter {
    Quote fromQuoteDTO(QuoteDTO quoteDTO);
    List<Quote> fromQuotePageDTO(QuotePageDTO quotePageDTO);
    List<Quote> fromSearchQuotesResponseDTO(SearchQuoteResponseDTO searchQuotesResponseDTO);
}
