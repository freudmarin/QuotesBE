package com.marin.socialnetwork.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserPostInteractionDTO {
    private Long id;
    private PostDTO post;
    private UserDTO user;
    private boolean liked;
    private boolean shared;
    private LocalDateTime addedAt;
    private LocalDateTime updatedAt;
}
