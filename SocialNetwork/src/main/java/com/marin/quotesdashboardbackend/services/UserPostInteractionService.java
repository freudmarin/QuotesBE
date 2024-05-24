package com.marin.quotesdashboardbackend.services;

import com.marin.quotesdashboardbackend.entities.Post;
import com.marin.quotesdashboardbackend.entities.User;
import com.marin.quotesdashboardbackend.entities.UserPostInteraction;
import com.marin.quotesdashboardbackend.repositories.PostRepository;
import com.marin.quotesdashboardbackend.repositories.UserPostInteractionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserPostInteractionService {

    private final UserPostInteractionRepository repository;

    private final PostRepository postRepository;
    private final UserPostInteractionRepository userPostInteractionRepository;

    public void likePost(User user, Post post) {
        UserPostInteraction interaction = repository.findByUserAndPost(user, post)
                .orElse(new UserPostInteraction(user , true, post, false));
        interaction.setAddedAt(LocalDateTime.now());
        repository.save(interaction);
    }

    public void unlikePost(User user, Post post) {
        repository.findByUserAndPost(user, post).ifPresent(interaction -> {
            if (interaction.isLiked()) {
                interaction.setLiked(false);
                interaction.setUpdatedAt(LocalDateTime.now());
                repository.save(interaction);
            }
        });
    }

    public int getLikesCount(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post not found"));
        return userPostInteractionRepository.countByPostAndLikedTrue(post);
    }
}
