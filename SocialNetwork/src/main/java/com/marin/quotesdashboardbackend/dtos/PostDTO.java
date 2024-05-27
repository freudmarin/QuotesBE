package com.marin.quotesdashboardbackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PostDTO {
    private QuoteDTO quote;

    private List<CommentDTO> comments;

    private UserDTO createdBy;

    private String text;

    private LocalDateTime addedAt;

    private LocalDateTime updatedAt;
}
