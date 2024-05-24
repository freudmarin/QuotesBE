package com.marin.quotesdashboardbackend.services;

import com.marin.quotesdashboardbackend.dtos.DTOMappings;
import com.marin.quotesdashboardbackend.dtos.UserProfileDTO;
import com.marin.quotesdashboardbackend.dtos.UserProfileUpdateDTO;
import com.marin.quotesdashboardbackend.entities.Author;
import com.marin.quotesdashboardbackend.entities.Tag;
import com.marin.quotesdashboardbackend.entities.User;
import com.marin.quotesdashboardbackend.repositories.AuthorRepository;
import com.marin.quotesdashboardbackend.repositories.TagRepository;
import com.marin.quotesdashboardbackend.repositories.UserRepository;
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

    public UserProfileDTO getUserProfile(User user) {
        return DTOMappings.fromUserToProfileDTO(user);
    }

    @Transactional
    public UserProfileDTO updateUserProfile(User user, UserProfileUpdateDTO updateDTO) {
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

        return getUserProfile(user);
    }


    public String uploadProfilePicture(User user, MultipartFile file) {
        String profilePictureUrl = fileStorageService.storeFile(file);
        user.setProfilePictureUrl(profilePictureUrl);
        userRepository.save(user);
        return profilePictureUrl;
    }
}
