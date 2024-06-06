package com.marin.socialnetwork.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class JwtLoginRequest {
    @NotEmpty
    @Email
    private String email;

    @NotEmpty
    private String password;

    // Getters and setters
}
