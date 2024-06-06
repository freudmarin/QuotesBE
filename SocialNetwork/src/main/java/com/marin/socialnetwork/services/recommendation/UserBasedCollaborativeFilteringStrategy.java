package com.marin.socialnetwork.services.recommendation;

import com.marin.socialnetwork.dtos.QuoteDTO;
import com.marin.socialnetwork.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserBasedCollaborativeFilteringStrategy implements RecommendationStrategy {
    private final UserBasedCollaborativeFiltering collaborativeFiltering;

    @Override
    public List<QuoteDTO> recommend(User user) {
        return collaborativeFiltering.recommendQuotes(user);
    }
}
