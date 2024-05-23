package com.marin.quotesdashboardbackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class UserActivityDTO {

    private UserDTO user;

    private QuoteDTO quote;

    private boolean liked;

    private LocalDateTime addedAt;

    private LocalDateTime updatedAt;
}
