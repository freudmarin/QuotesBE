package com.marin.quotableapi.models;

import lombok.Getter;

@Getter
public class Author {
    private String _id;
    private String name;
    private String bio;
    private String description;
    private String link;
    private int quoteCount;
    private String slug;
    private String dateAdded;
    private String dateModified;
}


