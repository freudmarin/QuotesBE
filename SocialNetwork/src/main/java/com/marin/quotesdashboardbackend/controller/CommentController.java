package com.marin.quotesdashboardbackend.controller;


import com.marin.quotesdashboardbackend.dtos.CommentDTO;
import com.marin.quotesdashboardbackend.entities.User;
import com.marin.quotesdashboardbackend.services.CommentService;
import com.marin.quotesdashboardbackend.services.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

    private User getLoggedInUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetailsService.loadUserEntityByUsername(userDetails.getUsername());
    }

    @PostMapping("{postId}")
    public ResponseEntity<CommentDTO> addComment(@PathVariable Long postId, @RequestBody Map<String, String> requestBody) {
        User user = getLoggedInUser();
        return ResponseEntity.ok(commentService.addComment(postId, user, requestBody.get("content")));
    }

    @GetMapping("{postId}")
    public ResponseEntity<List<CommentDTO>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsByPost(postId));
    }

    @PutMapping("{commentId}")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable Long commentId, @RequestBody Map<String, String> requestBody) {
        String content = requestBody.get("content");
        User user = getLoggedInUser();
        return ResponseEntity.ok(commentService.updateComment(commentId, user, content));
    }

    @DeleteMapping("{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        User user = getLoggedInUser();
        commentService.deleteComment(commentId, user);
        return ResponseEntity.noContent().build();
    }
}
