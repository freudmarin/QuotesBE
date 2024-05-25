package com.marin.quotesdashboardbackend.repositories;

import com.marin.quotesdashboardbackend.entities.Quote;

import java.util.List;

public interface QuoteRepositoryCustom {
    List<Quote> searchQuotes(String text, String author, List<String> tags);
}
