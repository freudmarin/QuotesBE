package com.marin.quotesdashboardbackend.services;

import com.marin.quotesdashboardbackend.dtos.CommentDTO;
import com.marin.quotesdashboardbackend.dtos.DTOMappings;
import com.marin.quotesdashboardbackend.entities.Comment;
import com.marin.quotesdashboardbackend.entities.Quote;
import com.marin.quotesdashboardbackend.entities.User;
import com.marin.quotesdashboardbackend.repositories.CommentRepository;
import com.marin.quotesdashboardbackend.repositories.QuoteRepository;
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

    private final QuoteRepository quoteRepository;

    public CommentDTO addComment(Long quoteId, User user, String content) {
        Quote quote = quoteRepository.findById(quoteId).orElseThrow(() -> new EntityNotFoundException("Quote not found"));
        Comment comment = new Comment();
        comment.setQuote(quote);
        comment.setUser(user);
        comment.setContent(content);
        comment.setAddedAt(LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);
        return DTOMappings.fromCommentToCommentDTO(savedComment);
    }

    public List<CommentDTO> getCommentsByQuote(Long quoteId) {
        Quote quote = quoteRepository.findById(quoteId).orElseThrow(() -> new EntityNotFoundException("Quote not found"));
        return commentRepository.findByQuote(quote).stream().map(DTOMappings::fromCommentToCommentDTO)
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
    }
}
