package com.marin.socialnetwork.services;

import com.marin.socialnetwork.entities.Post;
import com.marin.socialnetwork.entities.User;
import com.marin.socialnetwork.entities.UserPostInteraction;
import com.marin.socialnetwork.enums.FriendConnectionStatus;
import com.marin.socialnetwork.exceptions.UnauthorizedException;
import com.marin.socialnetwork.repositories.FriendConnectionRepository;
import com.marin.socialnetwork.repositories.PostRepository;
import com.marin.socialnetwork.repositories.UserPostInteractionRepository;
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
