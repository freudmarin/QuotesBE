package com.marin.quotesdashboardbackend.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@Builder
public class JwtResponse {
    private final String jwtToken;
}
