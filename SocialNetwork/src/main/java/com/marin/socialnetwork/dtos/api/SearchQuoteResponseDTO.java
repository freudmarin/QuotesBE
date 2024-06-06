package com.marin.socialnetwork.dtos.api;

import lombok.Getter;

import java.util.List;

@Getter
public class SearchQuoteResponseDTO {
    private List<QuoteDTO> results;
}
