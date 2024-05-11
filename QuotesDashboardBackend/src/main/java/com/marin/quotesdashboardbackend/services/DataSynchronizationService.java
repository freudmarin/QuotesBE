package com.marin.quotesdashboardbackend.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class DataSynchronizationService {

    private final AuthorService authorService;

    private final QuoteService quoteService;


    public DataSynchronizationService(AuthorService authorService, QuoteService quoteService) {
        this.authorService = authorService;
        this.quoteService = quoteService;
    }

    @Scheduled(fixedRate = 3600000)
    public void synchronizeAuthorsAndsQuotes() {
        authorService.syncAuthors();
        quoteService.syncQuotes();
    }
}
