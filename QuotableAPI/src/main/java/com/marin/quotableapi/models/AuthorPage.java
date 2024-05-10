package com.marin.quotableapi.models;

import lombok.Getter;

import java.util.List;

@Getter
public class AuthorPage {
    private int count;
    private int totalCount;
    private int page;
    private int totalPages;
    private int lastItemIndex;
    private List<Author> results;
}
