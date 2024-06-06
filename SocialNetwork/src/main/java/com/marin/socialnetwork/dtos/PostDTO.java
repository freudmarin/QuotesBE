package com.marin.socialnetwork.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PostDTO {

    private Long id;
    private QuoteDTO quote;

    private String text;

    private List<CommentDTO> comments;

    private String postPhotoUrl;

    private UserDTO user;

    private boolean isPublic;

    private LocalDateTime addedAt;

    private LocalDateTime updatedAt;
}
