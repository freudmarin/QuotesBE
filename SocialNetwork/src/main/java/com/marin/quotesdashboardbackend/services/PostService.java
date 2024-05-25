package com.marin.quotesdashboardbackend.services;

import com.marin.quotesdashboardbackend.dtos.DTOMappings;
import com.marin.quotesdashboardbackend.dtos.PostCreateUpdateDTO;
import com.marin.quotesdashboardbackend.dtos.PostDTO;
import com.marin.quotesdashboardbackend.entities.Post;
import com.marin.quotesdashboardbackend.entities.Quote;
import com.marin.quotesdashboardbackend.entities.User;
import com.marin.quotesdashboardbackend.repositories.PostRepository;
import com.marin.quotesdashboardbackend.repositories.UserPostInteractionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final QuoteService quoteService;

    private final UserPostInteractionRepository postInteractionRepository;

    public Post findById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Post not found"));
    }

    public PostDTO save(Long quoteId, User user, PostCreateUpdateDTO postDTO) {
        Quote quote = quoteService.findQuoteById(quoteId);
        Post post = new Post();
        post.setQuote(quote);
        post.setAuthor(user);
        post.setText(postDTO.getText());
        return DTOMappings.fromPosttoPostDTO(postRepository.save(post));
    }
}
