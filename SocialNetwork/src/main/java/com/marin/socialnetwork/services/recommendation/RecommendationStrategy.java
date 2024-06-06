package com.marin.socialnetwork.services.recommendation;

import com.marin.socialnetwork.dtos.QuoteDTO;
import com.marin.socialnetwork.entities.User;

import java.util.List;

public interface RecommendationStrategy {
    List<QuoteDTO> recommend(User user);
}
