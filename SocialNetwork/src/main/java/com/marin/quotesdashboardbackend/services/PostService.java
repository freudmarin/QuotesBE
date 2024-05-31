package com.marin.quotesdashboardbackend.services;

import com.marin.quotesdashboardbackend.dtos.DTOMappings;
import com.marin.quotesdashboardbackend.dtos.PostCreateUpdateDTO;
import com.marin.quotesdashboardbackend.dtos.PostDTO;
import com.marin.quotesdashboardbackend.entities.Post;
import com.marin.quotesdashboardbackend.entities.Quote;
import com.marin.quotesdashboardbackend.entities.User;
import com.marin.quotesdashboardbackend.enums.FriendConnectionStatus;
import com.marin.quotesdashboardbackend.exceptions.UnauthorizedException;
import com.marin.quotesdashboardbackend.repositories.FriendConnectionRepository;
import com.marin.quotesdashboardbackend.repositories.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final QuoteService quoteService;

    private final FriendConnectionRepository friendConnectionRepository;

    public Post getPostById(Long postId, User requestingUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        if (post.isPublic() || friendConnectionRepository.existsByUserAndFriendAndStatus(requestingUser, post.getUser(), FriendConnectionStatus.ACCEPTED)
                || friendConnectionRepository.existsByUserAndFriendAndStatus(post.getUser(), requestingUser, FriendConnectionStatus.ACCEPTED)
                || requestingUser.equals(post.getUser())) {
            return post;
        } else {
            throw new UnauthorizedException("User is not allowed to view this post.");
        }
    }

    public PostDTO createOrUpdatePost(PostCreateUpdateDTO postDto, User user) {
        Post post;
        if (postDto.getPostId() != null) {
            post = postRepository.findById(postDto.getPostId())
                    .orElseThrow(() -> new EntityNotFoundException("Post not found"));
            if (!post.getUser().equals(user)) {
                throw new UnauthorizedException("You are not authorized to update this post.");
            }
        } else {
            post = new Post();
            post.setUser(user);
        }

        Quote quote = quoteService.findQuoteById(postDto.getQuoteId());

        post.setQuote(quote);
        post.setText(postDto.getText());
        post.setPostPhotoUrl(postDto.getPostPhotoUrl());
        post.setUpdatedAt(LocalDateTime.now());
        post.setPublic(postDto.getIsPublic());

        return DTOMappings.INSTANCE.toPostDTO(postRepository.save(post));
    }


    public void deletePost(Long postId, User requestingUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        if (!post.getUser().equals(requestingUser)) {
            throw new UnauthorizedException("You are not authorized to delete this post.");
        }
        post.setDeleted(true);
        post.getComments().forEach(comment -> comment.setDeleted(true));
        postRepository.save(post);
    }

    public List<PostDTO> getUserPosts(Long authorId, User requestingUser) {
        return postRepository.findAccessiblePosts(authorId, requestingUser).stream().map(
                DTOMappings.INSTANCE::toPostDTO).toList();
    }

}
