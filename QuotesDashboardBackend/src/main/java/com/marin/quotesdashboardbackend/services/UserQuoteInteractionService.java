package com.marin.quotesdashboardbackend.services;

import com.marin.quotesdashboardbackend.entities.Quote;
import com.marin.quotesdashboardbackend.entities.User;
import com.marin.quotesdashboardbackend.entities.UserQuoteInteraction;
import com.marin.quotesdashboardbackend.repositories.UserQuoteInteractionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQuoteInteractionService {

    private final UserQuoteInteractionRepository repository;

    public void likeQuote(User user, Quote quote) {
        UserQuoteInteraction interaction = repository.findByUserAndQuote(user, quote)
                .orElse(new UserQuoteInteraction(user , true, quote, false));
        interaction.setLiked(true);
        repository.save(interaction);
    }

    public void unlikeQuote(User user, Quote quote) {
        repository.findByUserAndQuote(user, quote).ifPresent(interaction -> {
            interaction.setLiked(false);
            repository.save(interaction);
        });
    }
}
