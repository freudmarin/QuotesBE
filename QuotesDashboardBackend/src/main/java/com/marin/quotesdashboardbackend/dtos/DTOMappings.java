package com.marin.quotesdashboardbackend.dtos;

import com.marin.quotesdashboardbackend.entities.Author;
import com.marin.quotesdashboardbackend.entities.Quote;

public class DTOMappings {

    public static  QuoteDTO fromQuoteToQuoteDTO(Quote quote) {
        return new QuoteDTO(quote.getId(), quote.getText(), fromAuthorToAuthorDTO(quote.getAuthor()));
    }

    public static  AuthorDTO fromAuthorToAuthorDTO(Author author) {
        return new AuthorDTO(author.getId(), author.getName());
    }
}
