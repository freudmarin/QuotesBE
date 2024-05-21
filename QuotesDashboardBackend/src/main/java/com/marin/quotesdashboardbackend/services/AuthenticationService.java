package com.marin.quotesdashboardbackend.services;

import com.marin.quotesdashboardbackend.dtos.JwtLoginRequest;
import com.marin.quotesdashboardbackend.dtos.JwtResponse;
import com.marin.quotesdashboardbackend.dtos.JwtSignupRequest;
import com.marin.quotesdashboardbackend.entities.User;
import com.marin.quotesdashboardbackend.exceptions.UnauthorizedException;
import com.marin.quotesdashboardbackend.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final CustomUserDetailsService customUserDetailsService;

    private final TokenBlackListService tokenBlackListService;


    public JwtResponse registerUser(JwtSignupRequest signupRequest) {
        User newUser = new User();
        newUser.setName(signupRequest.getName());
        newUser.setEmail(signupRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        newUser.setRoles(Collections.singletonList("ROLE_USER"));
        userRepository.save(newUser);

        final var jwt = jwtService.generateToken(newUser.getUsername());
        return JwtResponse.builder().jwtToken(jwt).build();
    }

    public JwtResponse authenticateUser(JwtLoginRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword()));
        } catch (AuthenticationException e)  {
            throw new UnauthorizedException("Invalid email or password");
        }
        final UserDetails userDetails = customUserDetailsService.loadUserByUsername(authenticationRequest.getEmail());
        final String jwt = jwtService.generateToken(userDetails.getUsername());

        return JwtResponse.builder().jwtToken(jwt).build();
    }

    public void setPassword(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email"));
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication,
                        String token) {
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
    }
}
