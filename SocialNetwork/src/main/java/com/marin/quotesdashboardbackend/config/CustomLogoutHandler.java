package com.marin.quotesdashboardbackend.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLogoutHandler implements LogoutHandler {

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken) {
            try {
                // Clear the Spring Security context
                new SecurityContextLogoutHandler().logout(request, response, authentication);

                // Redirect to Google's logout URL
                String logoutUrl = "https://accounts.google.com/logout";
                response.sendRedirect(logoutUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
    }
}
