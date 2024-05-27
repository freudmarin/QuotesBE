package com.marin.quotesdashboardbackend.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCreateUpdateDTO {
    private Long quoteId;
    private String text;
    private String postPhotoUrl;
    private boolean isPublic;
}
