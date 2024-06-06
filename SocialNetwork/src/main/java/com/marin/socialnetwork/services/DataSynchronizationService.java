package com.marin.socialnetwork.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class DataSynchronizationService {

    private final AuthorService authorService;

    private final QuoteService quoteService;

    private final TagService tagService;

    public DataSynchronizationService(AuthorService authorService, QuoteService quoteService, TagService tagService) {
        this.authorService = authorService;
        this.quoteService = quoteService;
        this.tagService = tagService;
    }

    @Scheduled(fixedRate = 3600000)
    public void synchronizeAuthorsAndsQuotes() {
        authorService.syncAuthors();
        tagService.syncTags();
        quoteService.syncQuotes();
    }
}
