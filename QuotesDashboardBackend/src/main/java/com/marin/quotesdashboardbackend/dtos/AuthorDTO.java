package com.marin.quotesdashboardbackend.dtos;

import lombok.Getter;

@Getter
public class AuthorDTO {
    private String name;
    private String bio;
    private String description;
    private String link;
    private int quoteCount;
    private String slug;
    private String dateAdded;
    private String dateModified;
}
