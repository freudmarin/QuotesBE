package com.marin.quotesdashboardbackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class QuoteDTO {
    private Long id;

    private String text;

    private AuthorDTO author;

    private List<TagDTO> tags;

}
