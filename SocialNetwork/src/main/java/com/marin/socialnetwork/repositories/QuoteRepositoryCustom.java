package com.marin.socialnetwork.repositories;

import com.marin.socialnetwork.entities.Quote;

import java.util.List;

public interface QuoteRepositoryCustom {

    List<Quote> searchQuotes(String text, String author, List<String> tags);

}
