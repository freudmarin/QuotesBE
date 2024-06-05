package com.marin.quotesdashboardbackend.controllers;

import com.marin.quotesdashboardbackend.dtos.DTOMappings;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/posts")
@PreAuthorize("hasRole('USER')")
public class PostController {

    private final CustomUserDetailsService userDetailsService;

    private final PostService postService;

    private final UserPostInteractionService userPostInteractionService;

    private User getLoggedInUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetailsService.loadUserEntityByUsername(userDetails.getUsername());
    }

    @PostMapping("post")
    public ResponseEntity<PostDTO> createPost(@RequestBody PostCreateUpdateDTO postDTO) {
        User user = getLoggedInUser();
        return ResponseEntity.ok(postService.createOrUpdatePost(postDTO, user));
    }


    @PostMapping("{postId}/like")
    public ResponseEntity<Void> likePost(@PathVariable Long postId) {
        User user = getLoggedInUser();
        Post post = postService.getPostById(postId, user);
        userPostInteractionService.likePost(user,post);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{postId}/unlike")
    public ResponseEntity<Void> unlikePost(@PathVariable Long postId) {
        User user = getLoggedInUser();
        Post post = postService.getPostById(postId, user);
        userPostInteractionService.unlikePost(user, post);
        return ResponseEntity.ok().build();
    }

    @GetMapping("{postId}/likes-count")
    public ResponseEntity<Integer> getLikesCount(@PathVariable Long postId) {
        int likesCount = userPostInteractionService.getLikesCount(postId);
        return ResponseEntity.ok(likesCount);
    }

    @PutMapping("update")
    public ResponseEntity<PostDTO> updatePost(@RequestBody PostCreateUpdateDTO updatedPostDTO) {
        User requestingUser = getLoggedInUser();
        return ResponseEntity.ok(postService.createOrUpdatePost(updatedPostDTO, requestingUser));
    }

    @DeleteMapping("{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        User requestingUser = getLoggedInUser();
        postService.deletePost(postId, requestingUser);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("{postId}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long postId) {
        User requestingUser = getLoggedInUser();
        return ResponseEntity.ok(DTOMappings.
                INSTANCE.toPostDTO(postService.getPostById(postId, requestingUser)));
    }

    @GetMapping("user/{authorId}")
    public ResponseEntity<List<PostDTO>> getUserPosts(@PathVariable Long authorId) {
        User requestingUser = getLoggedInUser();
        return ResponseEntity.ok(postService.getUserPosts(authorId, requestingUser));
    }

}
