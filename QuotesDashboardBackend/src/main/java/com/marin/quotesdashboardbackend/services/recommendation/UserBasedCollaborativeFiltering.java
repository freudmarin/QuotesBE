package com.marin.quotesdashboardbackend.services.recommendation;


import com.marin.quotesdashboardbackend.dtos.DTOMappings;
import com.marin.quotesdashboardbackend.dtos.QuoteDTO;
import com.marin.quotesdashboardbackend.entities.User;
import com.marin.quotesdashboardbackend.entities.UserQuoteInteraction;
import com.marin.quotesdashboardbackend.repositories.UserQuoteInteractionRepository;
import com.marin.quotesdashboardbackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserBasedCollaborativeFiltering {

    private final UserRepository userRepository;

    private final UserQuoteInteractionRepository interactionRepository;


    public List<QuoteDTO> recommendQuotes(User user) {
        List<User> users = userRepository.findAll();
        Map<User, Double> similarityScores = new HashMap<>();

        for (User otherUser : users) {
            if (!otherUser.equals(user)) {
                double similarity = calculatePearsonCorrelation(user, otherUser);
                similarityScores.put(otherUser, similarity);
            }
        }

        List<User> mostSimilarUsers = similarityScores.entrySet().stream()
                .sorted(Map.Entry.<User, Double>comparingByValue().reversed())
                .limit(5) // Get top 5 most similar users
                .map(Map.Entry::getKey)
                .toList();

        return mostSimilarUsers.stream()
                .flatMap(similarUser -> interactionRepository.findByUserAndLikedTrue(similarUser).stream()
                        .map(UserQuoteInteraction::getQuote))
                .distinct()
                .map(DTOMappings::fromQuoteToQuoteDTO)
                .collect(Collectors.toList());
    }

    private double calculatePearsonCorrelation(User user1, User user2) {
        List<UserQuoteInteraction> user1Interactions = interactionRepository.findByUserAndLikedTrue(user1);
        List<UserQuoteInteraction> user2Interactions = interactionRepository.findByUserAndLikedTrue(user2);

        Map<Long, Boolean> user1LikedQuotes = user1Interactions.stream()
                .collect(Collectors.toMap(interaction -> interaction.getQuote().getId(), UserQuoteInteraction::isLiked));

        Map<Long, Boolean> user2LikedQuotes = user2Interactions.stream()
                .collect(Collectors.toMap(interaction -> interaction.getQuote().getId(), UserQuoteInteraction::isLiked));

        Set<Long> commonQuoteIds = new HashSet<>(user1LikedQuotes.keySet());
        commonQuoteIds.retainAll(user2LikedQuotes.keySet());

        if (commonQuoteIds.isEmpty()) {
            return 0.0;
        }

        int n = commonQuoteIds.size();

        int sum1 = commonQuoteIds.stream().mapToInt(quoteId -> user1LikedQuotes.get(quoteId) ? 1 : 0).sum();
        int sum2 = commonQuoteIds.stream().mapToInt(quoteId -> user2LikedQuotes.get(quoteId) ? 1 : 0).sum();

        int sum1Sq = commonQuoteIds.stream().mapToInt(quoteId -> (user1LikedQuotes.get(quoteId) ? 1 : 0) * (user1LikedQuotes.get(quoteId) ? 1 : 0)).sum();
        int sum2Sq = commonQuoteIds.stream().mapToInt(quoteId -> (user2LikedQuotes.get(quoteId) ? 1 : 0) * (user2LikedQuotes.get(quoteId) ? 1 : 0)).sum();

        int pSum = commonQuoteIds.stream().mapToInt(quoteId -> (user1LikedQuotes.get(quoteId) ? 1 : 0) * (user2LikedQuotes.get(quoteId) ? 1 : 0)).sum();

        double num = pSum - (sum1 * sum2 / (double) n);
        double den = Math.sqrt((sum1Sq - (sum1 * sum1 / (double) n)) * (sum2Sq - (sum2 * sum2 / (double) n)));

        if (den == 0) {
            log.info("Pearson Correlation: {}", 0);
            return 0.0;
        }

        log.info("Pearson Correlation: {}", num/den);

        return num / den;
    }
}
