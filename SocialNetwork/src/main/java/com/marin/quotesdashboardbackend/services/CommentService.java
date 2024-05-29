package com.marin.quotesdashboardbackend.services;

import com.marin.quotesdashboardbackend.dtos.CommentDTO;
import com.marin.quotesdashboardbackend.dtos.DTOMappings;
import com.marin.quotesdashboardbackend.entities.Comment;
import com.marin.quotesdashboardbackend.entities.Post;
import com.marin.quotesdashboardbackend.entities.User;
import com.marin.quotesdashboardbackend.enums.FriendConnectionStatus;
import com.marin.quotesdashboardbackend.exceptions.UnauthorizedException;
import com.marin.quotesdashboardbackend.repositories.CommentRepository;
import com.marin.quotesdashboardbackend.repositories.FriendConnectionRepository;
import com.marin.quotesdashboardbackend.repositories.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final FriendConnectionRepository friendConnectionRepository;
    private final CustomUserDetailsService customUserDetailsService;

    public CommentDTO addComment(Long postId, User user, String content) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post not found"));


        // Check if the post is public or the user is a friend of the post author or the user is the author
        if (!post.isPublic() && !isFriend(user, post.getUser()) && !post.getUser().equals(user)) {
            throw new UnauthorizedException("You are not authorized to comment on this post.");
        }

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent(content);
        comment.setAddedAt(LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);
        return DTOMappings.fromCommentToCommentDTO(savedComment);
    }

    public List<CommentDTO> getCommentsByPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post not found"));

        // Check if the post is public or the user is a friend of the post author
        User user = getLoggedInUser(); // Assuming you have a method to get the logged-in user
        if (!post.isPublic() && !isFriend(user, post.getUser()) && !post.getUser().equals(user)) {
            throw new UnauthorizedException("You are not authorized to view comments on this post.");
        }

        return post.getComments().stream().map(DTOMappings::fromCommentToCommentDTO)
                .collect(Collectors.toList());
    }

    public CommentDTO updateComment(Long commentId, User user, String content) {
        Comment comment = commentRepository.findByIdAndUser(commentId, user)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found or user not authorized"));

        // Check if the post is public or the user is a friend of the post author
        if (!comment.getPost().isPublic() && !isFriend(user, comment.getPost().getUser()) &&
                !comment.getPost().getUser().equals(user)) {
            throw new UnauthorizedException("You are not authorized to update this comment.");
        }

        // Ensure that the user updating the comment is the author of the comment
        if (!comment.getUser().equals(user)) {
            throw new UnauthorizedException("You are not authorized to update this comment.");
        }

        comment.setContent(content);
        comment.setUpdatedAt(LocalDateTime.now());
        Comment updatedComment = commentRepository.save(comment);
        return DTOMappings.fromCommentToCommentDTO(updatedComment);
    }

    @Transactional
    public void deleteComment(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        Post post = comment.getPost();

        // Check if the user is the author of the comment or the author of the post
        if (!comment.getUser().equals(user) && !post.getUser().equals(user)) {
            throw new UnauthorizedException("You are not authorized to delete this comment.");
        }

        comment.setDeleted(true);
        comment.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(comment);
    }

    private boolean isFriend(User user, User postAuthor) {
        return friendConnectionRepository.existsByUserAndFriendAndStatus(user, postAuthor, FriendConnectionStatus.ACCEPTED)
                || friendConnectionRepository.existsByUserAndFriendAndStatus(postAuthor, user, FriendConnectionStatus.ACCEPTED);
    }

    private User getLoggedInUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return customUserDetailsService.loadUserEntityByUsername(userDetails.getUsername());
    }
}
