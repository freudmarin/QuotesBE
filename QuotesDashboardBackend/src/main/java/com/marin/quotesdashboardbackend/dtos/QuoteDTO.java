package com.marin.quotesdashboardbackend.dtos;

import lombok.Getter;

import java.util.List;

@Getter
public class QuoteDTO {
    private String content;
    private String author;
    List<String> tags;
}
