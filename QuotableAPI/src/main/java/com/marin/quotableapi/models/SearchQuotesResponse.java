package com.marin.quotableapi.models;

import lombok.Getter;

import java.util.List;

@Getter
public class SearchQuotesResponse extends SearchResponse {
    private List<Quote> results;
}
