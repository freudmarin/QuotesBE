package com.marin.quotesdashboardbackend.services;

import com.marin.quotesdashboardbackend.dtos.QuoteDTO;
import com.marin.quotesdashboardbackend.entities.User;
import com.marin.quotesdashboardbackend.repositories.UserQuoteInteractionRepository;
import com.marin.quotesdashboardbackend.services.recommendation.RecommendationStrategyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommenadationService {

    private final UserQuoteInteractionRepository interactionRepository;

    private final RecommendationStrategyFactory strategyFactory;

    public List<QuoteDTO> recommendQuotes(User user) {
        List<QuoteDTO> recommendations = strategyFactory.getStrategy("user-based").recommend(user);
        if (recommendations.isEmpty()) {
            recommendations = strategyFactory.getStrategy("item-based").recommend(user);
        }
        if (recommendations.isEmpty()) {
            recommendations = strategyFactory.getStrategy("content").recommend(user);
        }
        return recommendations;
    }
}
