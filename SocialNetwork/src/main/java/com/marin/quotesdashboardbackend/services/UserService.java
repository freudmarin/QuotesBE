package com.marin.quotesdashboardbackend.services;

import com.marin.quotesdashboardbackend.dtos.DTOMappings;
import com.marin.quotesdashboardbackend.dtos.PostDTO;
import com.marin.quotesdashboardbackend.dtos.UserProfileDTO;
import com.marin.quotesdashboardbackend.dtos.UserProfileUpdateDTO;
import com.marin.quotesdashboardbackend.entities.Author;
import com.marin.quotesdashboardbackend.entities.Post;
import com.marin.quotesdashboardbackend.entities.Tag;
import com.marin.quotesdashboardbackend.entities.User;
import com.marin.quotesdashboardbackend.enums.FriendConnectionStatus;
import com.marin.quotesdashboardbackend.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final TagRepository tagRepository;

    private final AuthorRepository authorRepository;

    private final FileStorageService fileStorageService;

    private final FriendConnectionRepository friendConnectionRepository;

    private final PostRepository postRepository;

    public UserProfileDTO getUserProfile(Long userId, User requestingUser) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        boolean areFriends = friendConnectionRepository.existsByUserAndFriendAndStatus(requestingUser, user, FriendConnectionStatus.ACCEPTED)
                || friendConnectionRepository.existsByUserAndFriendAndStatus(user, requestingUser, FriendConnectionStatus.ACCEPTED);


        List<Post> posts;
        List<PostDTO> postsDTO = null;
        if (areFriends || requestingUser.equals(user)) {
            posts = postRepository.findByAuthorAndDeletedIsFalse(user);
            postsDTO= posts.stream()
                    .map(DTOMappings::fromPostToPostDTO)
                    .toList();
        }

        return DTOMappings.fromUserToProfileDTO(user, postsDTO);

    }

    @Transactional
    public void updateUserProfile(User user, UserProfileUpdateDTO updateDTO) {
        user.setName(updateDTO.getName());
        user.setBio(updateDTO.getBio());
        user.setSocialLinks(updateDTO.getSocialLinks());

        // Fetch all tags in a single query
        List<Tag> favoriteTags = tagRepository.findAllById(updateDTO.getFavouriteTagIds());
        user.setFavoriteTags(new HashSet<>(favoriteTags));

        // Fetch all authors in a single query
        List<Author> favoriteAuthors = authorRepository.findAllById(updateDTO.getFavouriteAuthorIds());
        user.setFavoriteAuthors(new HashSet<>(favoriteAuthors));

        userRepository.save(user);
    }


    public String uploadProfilePicture(User user, MultipartFile file) {
        String profilePictureUrl = fileStorageService.storeFile(file);
        user.setProfilePictureUrl(profilePictureUrl);
        userRepository.save(user);
        return profilePictureUrl;
    }
}
