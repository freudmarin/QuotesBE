package com.marin.socialnetwork.services.recommendation;

import com.marin.socialnetwork.dtos.DTOMappings;
import com.marin.socialnetwork.dtos.QuoteDTO;
import com.marin.socialnetwork.entities.Post;
import com.marin.socialnetwork.entities.Quote;
import com.marin.socialnetwork.entities.Tag;
import com.marin.socialnetwork.entities.User;
import com.marin.socialnetwork.repositories.QuoteRepository;
import com.marin.socialnetwork.repositories.UserPostInteractionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentBasedFiltering {

    private final UserPostInteractionRepository interactionRepository;
    private final QuoteRepository quoteRepository;

    public List<QuoteDTO> recommendQuotes(User user) {
        List<Quote> likedQuotes = interactionRepository.findPostsByUserAndLikedTrue(user).stream()
                .map(Post::getQuote).toList();
        List<Quote> allQuotes = quoteRepository.findAllWithTagsAndAuthors();

        Map<Quote, Double> quoteScores = new HashMap<>();

        for (Quote quote : allQuotes) {
            if (!likedQuotes.contains(quote)) {
                double score = 0.0;
                for (Quote likedQuote : likedQuotes) {
                    score += calculateSimilarity(likedQuote, quote);
                }
                quoteScores.put(quote, score);
            }
        }

        var quotesToRecommend =  quoteScores.entrySet().stream()
                .sorted(Map.Entry.<Quote, Double>comparingByValue().reversed())
                .limit(10)
                .map(Map.Entry::getKey)
                .map(DTOMappings.INSTANCE::toQuoteDTO)
                .toList();

        log.info("Recommended quotes in Content-based filtering: {}", quotesToRecommend.size());
        return quotesToRecommend;
    }

    private double calculateSimilarity(Quote quote1, Quote quote2) {
        double score = 0.0;

        if (quote1.getAuthor().equals(quote2.getAuthor())) {
            score += 1.0;
        }

        Set<Tag> tags1 = quote1.getTags();
        Set<Tag> tags2 = quote2.getTags();
        Set<Tag> commonTags = new HashSet<>(tags1);
        commonTags.retainAll(tags2);
        score += commonTags.size() / (double) Math.max(tags1.size(), tags2.size());

        return score;
    }
}
