package com.marin.quotableapi.models;

import lombok.Getter;

import java.util.List;

@Getter
public class SearchAuthorsResponse extends SearchResponse {
    private List<Author> results;
}
