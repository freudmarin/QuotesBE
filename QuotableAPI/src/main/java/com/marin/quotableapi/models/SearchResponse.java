package com.marin.quotableapi.models;

import lombok.Getter;

@Getter
public abstract class SearchResponse {
    private int count;
    private int totalCount;
    private int page;
    private int totalPages;
}
