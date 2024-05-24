package com.marin.quotesdashboardbackend.services.recommendation;

import com.marin.quotesdashboardbackend.dtos.QuoteDTO;
import com.marin.quotesdashboardbackend.entities.User;
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
