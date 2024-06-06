package com.marin.socialnetwork.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class QuoteDTO {
    private Long id;

    private String text;

    private AuthorDTO author;

    private List<TagDTO> tags;

}
