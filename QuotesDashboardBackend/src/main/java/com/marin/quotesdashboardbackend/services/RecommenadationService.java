package com.marin.quotesdashboardbackend.services;

import com.marin.quotesdashboardbackend.dtos.DTOMappings;
import com.marin.quotesdashboardbackend.dtos.QuoteDTO;
import com.marin.quotesdashboardbackend.entities.User;
import com.marin.quotesdashboardbackend.repositories.UserQuoteInteractionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommenadationService {

    private final UserQuoteInteractionRepository interactionRepository;
    public List<QuoteDTO> recommendQuotes(User user) {
        // Find users with similar likes
        List<User> similarUsers = interactionRepository.findUsersWithSimilarInterests(user.getId());
        // Gather recommended quotes
        List<QuoteDTO> recommendedQuotes = similarUsers.stream()
                .flatMap(similarUser -> interactionRepository.findByUserAndLikedTrue(similarUser).stream())
                .distinct()
                .map(DTOMappings::fromQuoteToQuoteDTO)
                .collect(Collectors.toList());
        return recommendedQuotes;
    }
}
