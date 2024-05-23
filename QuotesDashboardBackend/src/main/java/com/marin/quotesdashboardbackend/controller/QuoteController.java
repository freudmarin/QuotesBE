package com.marin.quotesdashboardbackend.controller;

import com.marin.quotesdashboardbackend.dtos.QuoteDTO;
import com.marin.quotesdashboardbackend.entities.Quote;
import com.marin.quotesdashboardbackend.entities.User;
import com.marin.quotesdashboardbackend.services.CustomUserDetailsService;
import com.marin.quotesdashboardbackend.services.QuoteService;
import com.marin.quotesdashboardbackend.services.RecommendationService;
import com.marin.quotesdashboardbackend.services.UserQuoteInteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/quotes")
@RequiredArgsConstructor
public class QuoteController {

    private final UserQuoteInteractionService interactionService;

    private final QuoteService quoteService;

    private final CustomUserDetailsService userDetailsService;

    private final RecommendationService recommenadationService;

    @PostMapping("{quoteId}/like")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> likeQuote(@PathVariable Long quoteId, @AuthenticationPrincipal
    org.springframework.security.core.userdetails.User userDetails) {
        User user = userDetailsService.loadUserEntityByUsername(userDetails.getUsername());
        Quote quote = quoteService.findQuoteById(quoteId);
        interactionService.likeQuote(user, quote);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{quoteId}/unlike")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> unlikeQuote(@PathVariable Long quoteId, @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        User user = userDetailsService.loadUserEntityByUsername(userDetails.getUsername());
        Quote quote = quoteService.findQuoteById(quoteId);
        interactionService.unlikeQuote(user, quote);
        return ResponseEntity.ok().build();
    }

    @GetMapping("tags")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<QuoteDTO>> getQuotesByTags(@RequestParam List<String> tags) {
        return ResponseEntity.ok(quoteService.getQuotesByTags(tags));
    }

    @GetMapping("{quoteId}/likes-count")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Integer> getLikesCount(@PathVariable Long quoteId) {
        int likesCount = quoteService.getLikesCount(quoteId);
        return ResponseEntity.ok(likesCount);
    }

    @GetMapping("recommendations")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<QuoteDTO>> getRecommendations(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        User user = userDetailsService.loadUserEntityByUsername(userDetails.getUsername());
        List<QuoteDTO> recommendations = recommenadationService.recommendQuotes(user);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("quote-of-the-day")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<QuoteDTO> getQuoteOfTheDay() {
        return ResponseEntity.ok(quoteService.selectQuoteOfTheDay());
    }
}
