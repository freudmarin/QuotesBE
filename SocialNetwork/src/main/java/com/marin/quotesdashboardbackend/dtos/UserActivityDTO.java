package com.marin.quotesdashboardbackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserActivityDTO {

    private UserDTO user;
    private PostDTO post;
    private List<UserPostInteractionDTO> interactions;
    private LocalDateTime addedAt;
    private LocalDateTime updatedAt;
}
