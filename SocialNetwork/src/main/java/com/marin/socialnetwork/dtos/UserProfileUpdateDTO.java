package com.marin.socialnetwork.dtos;


import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class UserProfileUpdateDTO {
    private String name;
    private String bio;
    private List<String> socialLinks;
    private Set<Long> favouriteTagIds;
    private Set<Long> favouriteAuthorIds;
}
