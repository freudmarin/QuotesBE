package com.marin.quotesdashboardbackend.controller;

import com.marin.quotesdashboardbackend.config.CustomLogoutHandler;
import com.marin.quotesdashboardbackend.dtos.JwtLoginRequest;
import com.marin.quotesdashboardbackend.dtos.JwtResponse;
import com.marin.quotesdashboardbackend.dtos.JwtSignupRequest;
import com.marin.quotesdashboardbackend.entities.User;
import com.marin.quotesdashboardbackend.exceptions.UnauthorizedException;
import com.marin.quotesdashboardbackend.repositories.UserRepository;
import com.marin.quotesdashboardbackend.services.CustomUserDetailsService;
import com.marin.quotesdashboardbackend.services.JwtService;
import com.marin.quotesdashboardbackend.services.TokenBlackListService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final CustomUserDetailsService customUserDetailsService;

    private final JwtService jwtService;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final TokenBlackListService tokenBlackListService;

    private final CustomLogoutHandler logoutHandler;

    @PostMapping("register")
    public ResponseEntity<JwtResponse> registerUser(@RequestBody JwtSignupRequest signupRequest) {
        User newUser = new User();
        newUser.setName(signupRequest.getName());
        newUser.setEmail(signupRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        newUser.setRoles(Collections.singletonList("ROLE_USER"));
        userRepository.save(newUser);
        final var jwt = jwtService.generateToken(newUser.getUsername());
        return ResponseEntity.ok(JwtResponse.builder().jwtToken(jwt).build());
    }

    @PostMapping("authenticate")
    public ResponseEntity<JwtResponse> createAuthenticationToken(@RequestBody JwtLoginRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword()));
        } catch (AuthenticationException e)  {
            throw new UnauthorizedException("Invalid email or password");
        }
        final UserDetails userDetails = customUserDetailsService.loadUserByUsername(authenticationRequest.getEmail());
        final String jwt = jwtService.generateToken(userDetails.getUsername());

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

    @PostMapping("logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication, @RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring(7);
            tokenBlackListService.blacklistToken(jwt);
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            if (authentication instanceof OAuth2AuthenticationToken) {
                try {
                    response.sendRedirect("https://accounts.google.com/Logout");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
