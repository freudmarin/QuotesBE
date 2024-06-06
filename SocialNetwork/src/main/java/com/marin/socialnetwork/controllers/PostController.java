package com.marin.socialnetwork.controllers;

import com.marin.socialnetwork.dtos.DTOMappings;
import com.marin.socialnetwork.dtos.PostCreateUpdateDTO;
import com.marin.socialnetwork.dtos.PostDTO;
import com.marin.socialnetwork.entities.Post;
import com.marin.socialnetwork.entities.User;
import com.marin.socialnetwork.services.CustomUserDetailsService;
import com.marin.socialnetwork.services.PostService;
import com.marin.socialnetwork.services.UserPostInteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<PostDTO> createPost(@RequestPart PostCreateUpdateDTO postDTO,
                                              @RequestPart("postPhoto") Optional<MultipartFile> postPhoto) {
        User user = getLoggedInUser();
        return ResponseEntity.ok(postService.createPost(postDTO, user, postPhoto.orElse(null)));
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

    @PutMapping("{postId}")
    public ResponseEntity<PostDTO> updatePost(@PathVariable("postId") Long postId, @RequestPart PostCreateUpdateDTO updatedPostDTO, @RequestPart("postPhoto") Optional<MultipartFile> postPhoto) {
        User requestingUser = getLoggedInUser();
        return ResponseEntity.ok(postService.updatePost(postId, updatedPostDTO, requestingUser,postPhoto.orElse(null)));
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
