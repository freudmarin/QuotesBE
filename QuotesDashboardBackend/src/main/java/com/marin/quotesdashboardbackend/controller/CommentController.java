package com.marin.quotesdashboardbackend.controller;


import com.marin.quotesdashboardbackend.dtos.CommentDTO;
import com.marin.quotesdashboardbackend.entities.User;
import com.marin.quotesdashboardbackend.services.CommentService;
import com.marin.quotesdashboardbackend.services.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/comments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class CommentController {

    private final CommentService commentService;

    private final CustomUserDetailsService userDetailsService;

    @PostMapping("{quoteId}")
    public ResponseEntity<CommentDTO> addComment(@PathVariable Long quoteId, @RequestBody Map<String, String> requestBody,
                                                @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        User user = userDetailsService.loadUserEntityByUsername(userDetails.getUsername());
        return ResponseEntity.ok(commentService.addComment(quoteId, user, requestBody.get("content")));
    }

    @GetMapping("{quoteId}")
    public ResponseEntity<List<CommentDTO>> getComments(@PathVariable Long quoteId) {
        return ResponseEntity.ok(commentService.getCommentsByQuote(quoteId));
    }

    @PutMapping("{commentId}")
    public CommentDTO updateComment(@PathVariable Long commentId, @RequestBody Map<String, String> requestBody,
                                    @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        String content = requestBody.get("content");
        User user = userDetailsService.loadUserEntityByUsername(userDetails.getUsername());
        return commentService.updateComment(commentId, user, content);
    }

    @DeleteMapping("{commentId}")
    public void deleteComment(@PathVariable Long commentId,
                              @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        User user = userDetailsService.loadUserEntityByUsername(userDetails.getUsername());
        commentService.deleteComment(commentId, user);
    }
}
