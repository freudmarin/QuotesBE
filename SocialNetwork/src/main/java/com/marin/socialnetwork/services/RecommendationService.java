package com.marin.socialnetwork.services;

import com.marin.socialnetwork.dtos.QuoteDTO;
import com.marin.socialnetwork.entities.User;
import com.marin.socialnetwork.services.recommendation.RecommendationStrategyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationStrategyFactory strategyFactory;

    public List<QuoteDTO> recommendQuotes(User user) {
        List<QuoteDTO> userBasedRecommendations = strategyFactory.getStrategy("user-based").recommend(user);
        List<QuoteDTO> itemBasedRecommendations = strategyFactory.getStrategy("item-based").recommend(user);
        List<QuoteDTO> contentBasedRecommendations = strategyFactory.getStrategy("content").recommend(user);

        Set<QuoteDTO> recommendationSet = new LinkedHashSet<>();
        recommendationSet.addAll(userBasedRecommendations);
        recommendationSet.addAll(itemBasedRecommendations);
        recommendationSet.addAll(contentBasedRecommendations);

        return new ArrayList<>(recommendationSet);
    }
}
