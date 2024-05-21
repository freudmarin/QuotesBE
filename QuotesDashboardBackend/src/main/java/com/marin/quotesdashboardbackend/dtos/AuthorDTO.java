package com.marin.quotesdashboardbackend.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthorDTO {
    private Long id;
    private String name;
    private String bio;
    private String description;
    private String link;

    public AuthorDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}




