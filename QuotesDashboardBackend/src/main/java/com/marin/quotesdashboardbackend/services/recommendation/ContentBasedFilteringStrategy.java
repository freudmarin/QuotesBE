package com.marin.quotesdashboardbackend.services.recommendation;

import com.marin.quotesdashboardbackend.dtos.QuoteDTO;
import com.marin.quotesdashboardbackend.entities.User;
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
