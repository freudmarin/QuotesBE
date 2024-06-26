package com.marin.socialnetwork.services;

import com.marin.socialnetwork.dtos.CommentDTO;
import com.marin.socialnetwork.dtos.DTOMappings;
import com.marin.socialnetwork.entities.Comment;
import com.marin.socialnetwork.entities.Post;
import com.marin.socialnetwork.entities.User;
import com.marin.socialnetwork.enums.FriendConnectionStatus;
import com.marin.socialnetwork.exceptions.UnauthorizedException;
import com.marin.socialnetwork.models.Notification;
import com.marin.socialnetwork.repositories.CommentRepository;
import com.marin.socialnetwork.repositories.FriendConnectionRepository;
import com.marin.socialnetwork.repositories.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;

    private final PostRepository postRepository;

    private final FriendConnectionRepository friendConnectionRepository;

    private final CustomUserDetailsService customUserDetailsService;

    private final SimpMessagingTemplate messagingTemplate;

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

        Notification notification = new Notification();
        notification.setType("COMMENT");
        notification.setMessage(user.getName() + " commented on your post");
        notification.setPostId(postId);

        messagingTemplate.convertAndSend("/topic/notifications", notification);
        log.info("Notification sent: {}", notification);
        return DTOMappings.INSTANCE.toCommentDTO(savedComment);
    }

    public List<CommentDTO> getCommentsByPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post not found"));

        // Check if the post is public or the user is a friend of the post author
        User user = getLoggedInUser(); // Assuming you have a method to get the logged-in user
        if (!post.isPublic() && !isFriend(user, post.getUser()) && !post.getUser().equals(user)) {
            throw new UnauthorizedException("You are not authorized to view comments on this post.");
        }

        return post.getComments().stream().map(DTOMappings.INSTANCE::toCommentDTO).collect(Collectors.toList());
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
        return DTOMappings.INSTANCE.toCommentDTO(updatedComment);
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
