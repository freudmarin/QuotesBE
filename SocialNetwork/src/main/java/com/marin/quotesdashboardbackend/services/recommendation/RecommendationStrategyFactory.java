package com.marin.quotesdashboardbackend.services.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecommendationStrategyFactory {

    private final UserBasedCollaborativeFilteringStrategy userBasedCollaborativeFiltering;

    private final ItemBasedCollaborativeFilteringStrategy itemBasedCollaborativeFiltering;
    private final ContentBasedFilteringStrategy contentBasedFiltering;

    public RecommendationStrategy getStrategy(String type) {
        return switch (type) {
            case "user-based" -> userBasedCollaborativeFiltering;
            case "item-based" -> itemBasedCollaborativeFiltering;
            case "content" -> contentBasedFiltering;
            default -> throw new IllegalArgumentException("Unknown strategy type");
        };
    }
}
