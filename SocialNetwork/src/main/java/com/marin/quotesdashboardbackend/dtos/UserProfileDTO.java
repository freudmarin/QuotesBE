package com.marin.quotesdashboardbackend.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class UserProfileDTO {
    private Long id;
    private String email;
    private String name;
    private String profilePictureUrl;
    private String bio;
    private List<String> socialLinks;
    private Set<TagDTO> favoriteTags;
    private Set<AuthorDTO> favoriteAuthors;
}
