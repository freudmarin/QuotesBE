package com.marin.socialnetwork.services;

import com.marin.socialnetwork.dtos.DTOMappings;
import com.marin.socialnetwork.dtos.PostCreateUpdateDTO;
import com.marin.socialnetwork.dtos.PostDTO;
import com.marin.socialnetwork.entities.Post;
import com.marin.socialnetwork.entities.Quote;
import com.marin.socialnetwork.entities.User;
import com.marin.socialnetwork.enums.FriendConnectionStatus;
import com.marin.socialnetwork.exceptions.UnauthorizedException;
import com.marin.socialnetwork.repositories.FriendConnectionRepository;
import com.marin.socialnetwork.repositories.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final QuoteService quoteService;

    private final FriendConnectionRepository friendConnectionRepository;

    private final FileStorageService fileStorageService;

    public Post getPostById(Long postId, User requestingUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        if (post.isPublic() || friendConnectionRepository.existsByUserAndFriendAndStatus(requestingUser, post.getUser(), FriendConnectionStatus.ACCEPTED)
                || friendConnectionRepository.existsByUserAndFriendAndStatus(post.getUser(), requestingUser, FriendConnectionStatus.ACCEPTED)
                || requestingUser.equals(post.getUser())) {
            return post;
        } else {
            throw new UnauthorizedException("User is not allowed to view this post.");
        }
    }

    public PostDTO updatePost(Long postId, PostCreateUpdateDTO postDto, User user, MultipartFile file) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        if (!post.getUser().equals(user)) {
            throw new UnauthorizedException("You are not authorized to update this post.");
        }

        return getPostDTO(postDto, file, post);

    }

    public PostDTO createPost(PostCreateUpdateDTO postDto, User user, MultipartFile file) {
        Post post = new Post();
        post.setUser(user);
        return getPostDTO(postDto, file, post);
    }

    private PostDTO getPostDTO(PostCreateUpdateDTO postDto, MultipartFile file, Post post) {
        Quote quote = quoteService.findQuoteById(postDto.getQuoteId());

        post.setQuote(quote);
        post.setText(postDto.getText());
        post.setUpdatedAt(LocalDateTime.now());
        post.setPublic(postDto.getIsPublic());

        if (file != null && !file.isEmpty()){
            String uploadedPhotoUrl = fileStorageService.storeFile(file);
            post.setPostPhotoUrl(uploadedPhotoUrl);
        }
        return DTOMappings.INSTANCE.toPostDTO(postRepository.save(post));
    }


    public void deletePost(Long postId, User requestingUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        if (!post.getUser().equals(requestingUser)) {
            throw new UnauthorizedException("You are not authorized to delete this post.");
        }
        post.setDeleted(true);
        post.getComments().forEach(comment -> comment.setDeleted(true));
        postRepository.save(post);
    }

    public List<PostDTO> getUserPosts(Long authorId, User requestingUser) {
        return postRepository.findAccessiblePosts(authorId, requestingUser).stream().map(
                DTOMappings.INSTANCE::toPostDTO).toList();
    }

}
