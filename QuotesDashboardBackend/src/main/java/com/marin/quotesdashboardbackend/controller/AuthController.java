package com.marin.quotesdashboardbackend.controller;

import com.marin.quotesdashboardbackend.dtos.JwtLoginRequest;
import com.marin.quotesdashboardbackend.dtos.JwtResponse;
import com.marin.quotesdashboardbackend.dtos.JwtSignupRequest;
import com.marin.quotesdashboardbackend.entities.User;
import com.marin.quotesdashboardbackend.repositories.UserRepository;
import com.marin.quotesdashboardbackend.services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final UserDetailsService userDetailsService;

    private final JwtService jwtService;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @PostMapping("register")
    public ResponseEntity<JwtResponse> registerUser(@RequestBody JwtSignupRequest signupRequest) {
        User newUser = new User();
        newUser.setName(signupRequest.getName());
        newUser.setEmail(signupRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        newUser.setRoles(Collections.singletonList("ROLE_USER"));
        userRepository.save(newUser);
        final var jwt = jwtService.generateToken(newUser.getEmail());
        return ResponseEntity.ok(JwtResponse.builder().jwtToken(jwt).build());
    }

    @PostMapping("authenticate")
    public ResponseEntity<JwtResponse> createAuthenticationToken(@RequestBody JwtLoginRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword()));
        } catch (AuthenticationException e) {
            throw new RuntimeException("Incorrect username or password", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
        final String jwt = jwtService.generateToken(userDetails.getUsername());

        return ResponseEntity.ok(JwtResponse.builder().jwtToken(jwt).build());
    }

    @GetMapping("user")
    public Map<String, Object> getUser(@AuthenticationPrincipal OAuth2User principal) {
        if (principal != null) {
            return Map.of(
                    "name", principal.getAttribute("name"),
                    "email", principal.getAttribute("email")
            );
        } else {
            return Map.of("error", "No authenticated useravailable.");
        }
    }
}
