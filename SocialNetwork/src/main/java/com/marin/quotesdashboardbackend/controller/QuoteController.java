package com.marin.quotesdashboardbackend.controller;

import com.marin.quotesdashboardbackend.dtos.QuoteDTO;
import com.marin.quotesdashboardbackend.entities.User;
import com.marin.quotesdashboardbackend.services.CustomUserDetailsService;
import com.marin.quotesdashboardbackend.services.QuoteService;
import com.marin.quotesdashboardbackend.services.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/quotes")
@RequiredArgsConstructor
public class QuoteController {

    private final QuoteService quoteService;

    private final CustomUserDetailsService userDetailsService;

    private final RecommendationService recommenadationService;

    @GetMapping("tags")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<QuoteDTO>> getQuotesByTags(@RequestParam List<String> tags) {
        return ResponseEntity.ok(quoteService.getQuotesByTags(tags));
    }

    @GetMapping("search")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<QuoteDTO>> searchQuotes(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) List<String> tags) {
        return ResponseEntity.ok(quoteService.searchQuotes(text, author, tags));
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
