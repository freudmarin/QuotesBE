package com.marin.quotesdashboardbackend.config;

import com.marin.quotesdashboardbackend.entities.User;
import com.marin.quotesdashboardbackend.repositories.UserRepository;
import com.marin.quotesdashboardbackend.services.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public OAuth2SuccessHandler(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String email = oauthToken.getPrincipal().getAttribute("email");

        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setName(oauthToken.getPrincipal().getAttribute("name"));
            user.setRoles(Collections.singletonList("ROLE_USER"));
            userRepository.save(user);
        }

        String token = jwtService.generateToken(email);
        response.setHeader("Authorization", "Bearer " + token);
        response.getWriter().write("{\"token\":\"" + token + "\"}");
    }
}
