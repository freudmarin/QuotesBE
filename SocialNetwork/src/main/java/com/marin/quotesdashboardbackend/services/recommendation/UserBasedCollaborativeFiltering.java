package com.marin.quotesdashboardbackend.services.recommendation;


import com.marin.quotesdashboardbackend.dtos.DTOMappings;
import com.marin.quotesdashboardbackend.dtos.QuoteDTO;
import com.marin.quotesdashboardbackend.entities.Quote;
import com.marin.quotesdashboardbackend.entities.User;
import com.marin.quotesdashboardbackend.entities.UserPostInteraction;
import com.marin.quotesdashboardbackend.repositories.UserPostInteractionRepository;
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
    private final UserPostInteractionRepository interactionRepository;

    public List<QuoteDTO> recommendQuotes(User user) {
        List<User> users = userRepository.findAll();
        List<UserPostInteraction> allInteractions = interactionRepository.findAllLikedPosts();

        Map<User, List<Quote>> userToQuotesMap = new HashMap<>();
        for (UserPostInteraction interaction : allInteractions) {
            userToQuotesMap.computeIfAbsent(interaction.getUser(), k -> new ArrayList<>()).add(interaction.getPost().getQuote());
        }

        Map<User, Double> similarityScores = new HashMap<>();
        for (User otherUser : users) {
            if (!otherUser.equals(user)) {
                double similarity = calculatePearsonCorrelation(user, otherUser, userToQuotesMap);
                similarityScores.put(otherUser, similarity);
            }
        }

        List<User> mostSimilarUsers = similarityScores.entrySet().stream()
                .sorted(Map.Entry.<User, Double>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .toList();

        Set<Quote> recommendedQuotes = new HashSet<>();
        for (User similarUser : mostSimilarUsers) {
            recommendedQuotes.addAll(userToQuotesMap.getOrDefault(similarUser, Collections.emptyList()));
        }

        //userToQuotesMap.getOrDefault(user, Collections.emptyList()).forEach(recommendedQuotes::remove);

        List<QuoteDTO> quotesToRecommend = recommendedQuotes.stream()
                .map(DTOMappings.INSTANCE::toQuoteDTO)
                .collect(Collectors.toList());

        log.info("User-based collaborative filtering quotes size: {}", quotesToRecommend.size());
        log.info("User-based collaborative filtering quotes: {}", quotesToRecommend);

        return quotesToRecommend;
    }

    private double calculatePearsonCorrelation(User user1, User user2, Map<User, List<Quote>> userToQuotesMap) {
        List<Quote> user1Quotes = userToQuotesMap.getOrDefault(user1, Collections.emptyList());
        List<Quote> user2Quotes = userToQuotesMap.getOrDefault(user2, Collections.emptyList());

        Set<Quote> commonQuotes = new HashSet<>(user1Quotes);
        commonQuotes.retainAll(user2Quotes);

        if (commonQuotes.isEmpty()) {
            return 0.0;
        }

        int n = commonQuotes.size();
        int sum1 = user1Quotes.stream().mapToInt(quote -> commonQuotes.contains(quote) ? 1 : 0).sum();
        int sum2 = user2Quotes.stream().mapToInt(quote -> commonQuotes.contains(quote) ? 1 : 0).sum();
        int sum1Sq = user1Quotes.stream().mapToInt(quote -> commonQuotes.contains(quote) ? 1 : 0).map(x -> x * x).sum();
        int sum2Sq = user2Quotes.stream().mapToInt(quote -> commonQuotes.contains(quote) ? 1 : 0).map(x -> x * x).sum();
        int pSum = commonQuotes.stream().mapToInt(quote -> (user1Quotes.contains(quote) ? 1 : 0) * (user2Quotes.contains(quote) ? 1 : 0)).sum();

        double num = pSum - (sum1 * sum2 / (double) n);
        double den = Math.sqrt((sum1Sq - (sum1 * sum1 / (double) n)) * (sum2Sq - (sum2 * sum2 / (double) n)));

        if (den == 0) {
            log.info("Pearson Correlation: {}", 0);
            return 0.0;
        }

        double correlation = num / den;
        log.info("Pearson Correlation: {}", correlation);

        return correlation;
    }
}
