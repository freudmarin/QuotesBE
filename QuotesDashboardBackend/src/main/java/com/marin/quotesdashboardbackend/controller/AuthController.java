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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

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
        final var jwt = jwtService.generateToken(newUser);
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
        final var user = userRepository.findByEmail(authenticationRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        final String jwt = jwtService.generateToken(user);

        return ResponseEntity.ok(JwtResponse.builder().jwtToken(jwt).build());
    }


    @PostMapping("/set-password")
    public ResponseEntity<?> setPassword(@RequestParam String email, @RequestParam String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email"));
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return ResponseEntity.ok("Password set successfully");
    }
}
