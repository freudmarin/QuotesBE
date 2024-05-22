package com.marin.quotesdashboardbackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CommentDTO {

    private Long id;

    private String content;
}
