package com.marin.quotesdashboardbackend.dtos;

import lombok.Getter;

import java.util.List;

@Getter
public class QuotePageDTO {
    private List<QuoteDTO> quotes;
    private int totalPages;
}
