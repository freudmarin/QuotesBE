package com.marin.quotesdashboardbackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class PostDTO {
    private QuoteDTO quote;

    private UserDTO createdBy;

    private String text;

    private LocalDateTime addedAt;

    private LocalDateTime updatedAt;
}
