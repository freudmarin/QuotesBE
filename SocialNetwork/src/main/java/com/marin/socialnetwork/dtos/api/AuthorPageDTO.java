package com.marin.socialnetwork.dtos.api;

import lombok.Getter;

import java.util.List;

@Getter
public class AuthorPageDTO {
    private int count;
    private int totalCount;
    private int page;
    private int totalPages;
    private int lastItemIndex;
    private List<AuthorDTO> results;
}
