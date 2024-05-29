package com.marin.quotesdashboardbackend.services;

import com.marin.quotesdashboardbackend.entities.Post;
import com.marin.quotesdashboardbackend.entities.User;
import com.marin.quotesdashboardbackend.entities.UserPostInteraction;
import com.marin.quotesdashboardbackend.enums.FriendConnectionStatus;
import com.marin.quotesdashboardbackend.exceptions.UnauthorizedException;
import com.marin.quotesdashboardbackend.repositories.FriendConnectionRepository;
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

    private final FriendConnectionRepository friendConnectionRepository;

    public void likePost(User user, Post post) {
        if (post.isPublic() || friendConnectionRepository.existsByUserAndFriendAndStatus(user, post.getUser(), FriendConnectionStatus.ACCEPTED)
                || friendConnectionRepository.existsByUserAndFriendAndStatus(post.getUser(), user, FriendConnectionStatus.ACCEPTED)
                || user.equals(post.getUser())) {
            UserPostInteraction interaction = new UserPostInteraction(user, true, post, false);
            interaction.setAddedAt(LocalDateTime.now());
            repository.save(interaction);
        } else {
            throw new UnauthorizedException("User with id " + user.getId() + " is not allowed to like this post.");
        }
    }

    public void unlikePost(User user, Post post) {
        if (post.isPublic() || friendConnectionRepository.existsByUserAndFriendAndStatus(user, post.getUser(), FriendConnectionStatus.ACCEPTED)
                || friendConnectionRepository.existsByUserAndFriendAndStatus(post.getUser(), user, FriendConnectionStatus.ACCEPTED)
                || user.equals(post.getUser())) {
            UserPostInteraction interaction = repository.findByUserAndPost(user, post).orElseThrow(() ->
                    new EntityNotFoundException("Interaction not found"));
            if (interaction.isLiked()) {
                interaction.setLiked(false);
                interaction.setUpdatedAt(LocalDateTime.now());
                repository.save(interaction);
            }
        } else {
            throw new UnauthorizedException("User with id " + user.getId() + " is not allowed to unlike this post.");
        }
    }

    public int getLikesCount(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post not found"));
        return userPostInteractionRepository.countByPostAndLikedTrue(post);
    }
}
