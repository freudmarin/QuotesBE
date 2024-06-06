package com.marin.socialnetwork.services.recommendation;

import com.marin.socialnetwork.dtos.QuoteDTO;
import com.marin.socialnetwork.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ContentBasedFilteringStrategy implements RecommendationStrategy {

    private final ContentBasedFiltering contentBasedFiltering;

    @Override
    public List<QuoteDTO> recommend(User user) {
        return contentBasedFiltering.recommendQuotes(user);
    }
}
