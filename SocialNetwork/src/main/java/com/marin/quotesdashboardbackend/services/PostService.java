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

    public PostDTO save(User user, PostCreateUpdateDTO postDTO) {
        Quote quote = quoteService.findQuoteById(postDTO.getQuoteId());
        Post post = new Post();
        post.setQuote(quote);
        post.setAuthor(user);
        post.setText(postDTO.getText());
        post.setPublic(postDTO.isPublic());
        return DTOMappings.fromPostToPostDTO(postRepository.save(post));
    }

    public Post getPostById(Long postId, User requestingUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        if (post.isPublic() || friendConnectionRepository.existsByUserAndFriendAndStatus(requestingUser, post.getAuthor(), FriendConnectionStatus.ACCEPTED)
                || friendConnectionRepository.existsByUserAndFriendAndStatus(post.getAuthor(), requestingUser, FriendConnectionStatus.ACCEPTED)
                || requestingUser.equals(post.getAuthor())) {
            return post;
        } else {
            throw new UnauthorizedException("User is not allowed to view this post.");
        }
    }

    public PostDTO updatePost(Long postId, PostCreateUpdateDTO updatedPost, User requestingUser) {
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        if (!existingPost.getAuthor().equals(requestingUser)) {
            throw new UnauthorizedException("You are not authorized to update this post.");
        }
        Quote newQuote = quoteService.findQuoteById(updatedPost.getQuoteId());
        existingPost.setQuote(newQuote);
        existingPost.setText(updatedPost.getText());
        existingPost.setPostPhotoUrl(updatedPost.getPostPhotoUrl());
        existingPost.setUpdatedAt(LocalDateTime.now());
        return DTOMappings.fromPostToPostDTO(postRepository.save(existingPost));
    }


    public void deletePost(Long postId, User requestingUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        if (!post.getAuthor().equals(requestingUser)) {
            throw new UnauthorizedException("You are not authorized to delete this post.");
        }
        post.setDeleted(true);
        post.getComments().forEach(comment -> comment.setDeleted(true));
        postRepository.save(post);
    }

    public List<PostDTO> getUserPosts(Long authorId, User requestingUser) {
        return postRepository.findAccessiblePosts(authorId, requestingUser).stream().map(
                DTOMappings::fromPostToPostDTO).toList();
    }

}
