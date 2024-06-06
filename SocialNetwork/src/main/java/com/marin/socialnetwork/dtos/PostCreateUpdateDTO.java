package com.marin.socialnetwork.dtos;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCreateUpdateDTO {
    @Nullable
    private Long postId;
    private Long quoteId;
    private String text;
    private Boolean isPublic;
}
