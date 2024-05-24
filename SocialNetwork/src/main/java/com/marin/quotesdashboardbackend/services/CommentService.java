package com.marin.quotesdashboardbackend.services;

import com.marin.quotesdashboardbackend.dtos.CommentDTO;
import com.marin.quotesdashboardbackend.dtos.DTOMappings;
import com.marin.quotesdashboardbackend.entities.Comment;
import com.marin.quotesdashboardbackend.entities.Post;
import com.marin.quotesdashboardbackend.entities.User;
import com.marin.quotesdashboardbackend.repositories.CommentRepository;
import com.marin.quotesdashboardbackend.repositories.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentDTO addComment(Long quoteId, User user, String content) {
        Post post = postRepository.findById(quoteId).orElseThrow(() -> new EntityNotFoundException("Quote not found"));
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
        return commentRepository.findByPost(post).stream().map(DTOMappings::fromCommentToCommentDTO)
                .collect(Collectors.toList());
    }

    public CommentDTO updateComment(Long commentId, User user, String content) {
        Comment comment = commentRepository.findByIdAndUser(commentId, user)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found or user not authorized"));
        comment.setContent(content);
        comment.setUpdatedAt(LocalDateTime.now());
        Comment updatedComment = commentRepository.save(comment);
        return DTOMappings.fromCommentToCommentDTO(updatedComment);
    }

    @Transactional
    public void deleteComment(Long commentId, User user) {
        Comment comment = commentRepository.findByIdAndUser(commentId, user)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found or user not authorized"));
        comment.setDeleted(true);
        comment.setUpdatedAt(LocalDateTime.now());
    }
}
