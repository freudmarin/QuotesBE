package com.marin.quotesdashboardbackend.services.recommendation;

import com.marin.quotesdashboardbackend.dtos.QuoteDTO;
import com.marin.quotesdashboardbackend.entities.User;

import java.util.List;

public interface RecommendationStrategy {
    List<QuoteDTO> recommend(User user);
}
