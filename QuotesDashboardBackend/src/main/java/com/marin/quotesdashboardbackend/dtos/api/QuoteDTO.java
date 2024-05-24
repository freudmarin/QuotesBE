package com.marin.quotesdashboardbackend.dtos.api;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuoteDTO {
    private String content;
    private String author;
    List<String> tags;
}
