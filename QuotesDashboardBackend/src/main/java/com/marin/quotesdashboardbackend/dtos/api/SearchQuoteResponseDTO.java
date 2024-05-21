package com.marin.quotesdashboardbackend.dtos.api;

import lombok.Getter;

import java.util.List;

@Getter
public class SearchQuoteResponseDTO {
    private List<QuoteDTO> results;
}
