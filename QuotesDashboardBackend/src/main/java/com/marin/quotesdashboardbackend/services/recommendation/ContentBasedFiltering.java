package com.marin.quotesdashboardbackend.services.recommendation;


import com.marin.quotesdashboardbackend.dtos.DTOMappings;
import com.marin.quotesdashboardbackend.dtos.QuoteDTO;
import com.marin.quotesdashboardbackend.entities.Quote;
import com.marin.quotesdashboardbackend.entities.User;
import com.marin.quotesdashboardbackend.repositories.QuoteRepository;
import com.marin.quotesdashboardbackend.repositories.UserQuoteInteractionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContentBasedFiltering {

    private final UserQuoteInteractionRepository interactionRepository;

    private final QuoteRepository quoteRepository;

    public List<QuoteDTO> recommendQuotes(User user) {
        List<Quote> likedQuotes = interactionRepository.findQuotesByUserAndLikedTrue(user);
        List<Quote> allQuotes = quoteRepository.findAll();

        // Implement content-based filtering logic
        return allQuotes.stream()
                .filter(quote -> !likedQuotes.contains(quote))
                .map(DTOMappings::fromQuoteToQuoteDTO)
                .collect(Collectors.toList());
    }
}
