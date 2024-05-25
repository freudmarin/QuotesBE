package com.marin.quotesdashboardbackend.controller;

import com.marin.quotesdashboardbackend.dtos.PostCreateUpdateDTO;
import com.marin.quotesdashboardbackend.dtos.PostDTO;
import com.marin.quotesdashboardbackend.entities.Post;
import com.marin.quotesdashboardbackend.entities.User;
import com.marin.quotesdashboardbackend.services.CustomUserDetailsService;
import com.marin.quotesdashboardbackend.services.PostService;
import com.marin.quotesdashboardbackend.services.UserPostInteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/posts")
@PreAuthorize("hasRole('USER')")
public class PostController {

    private final CustomUserDetailsService userDetailsService;
    private final PostService postService;
    private final UserPostInteractionService userPostInteractionService;

    @PostMapping("{quoteId}/post")
    public ResponseEntity<PostDTO> createPost(@PathVariable Long quoteId, @AuthenticationPrincipal
    org.springframework.security.core.userdetails.User userDetails, @RequestBody PostCreateUpdateDTO postDTO) {
        User user = userDetailsService.loadUserEntityByUsername(userDetails.getUsername());
        return ResponseEntity.ok(postService.save(quoteId, user, postDTO));
    }

    /*TODO Update a post*/

    @PostMapping("{postId}/like")
    public ResponseEntity<Void> likePost(@PathVariable Long postId, @AuthenticationPrincipal
    org.springframework.security.core.userdetails.User userDetails) {
        User user = userDetailsService.loadUserEntityByUsername(userDetails.getUsername());
        Post post = postService.findById(postId);
        userPostInteractionService.likePost(user,post);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{postId}/unlike")
    public ResponseEntity<Void> unlikePost(@PathVariable Long postId, @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        User user = userDetailsService.loadUserEntityByUsername(userDetails.getUsername());
        Post post = postService.findById(postId);
        userPostInteractionService.unlikePost(user, post);
        return ResponseEntity.ok().build();
    }

    @GetMapping("{postId}/likes-count")
    public ResponseEntity<Integer> getLikesCount(@PathVariable Long postId) {
        int likesCount = userPostInteractionService.getLikesCount(postId);
        return ResponseEntity.ok(likesCount);
    }
}
