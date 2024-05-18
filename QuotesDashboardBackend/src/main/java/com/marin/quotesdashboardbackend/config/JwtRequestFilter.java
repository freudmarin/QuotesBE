package com.marin.quotesdashboardbackend.config;

import com.marin.quotesdashboardbackend.services.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;

    private final JwtService jwtService;

    public JwtRequestFilter(UserDetailsService userDetailsService, JwtService jwtService) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;

        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/oauth2/") || requestURI.startsWith("/login/oauth2/")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (StringUtils.isEmpty(requestTokenHeader) || !StringUtils.startsWith(requestTokenHeader, "Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        log.info("Jwt Request filter invoked");

        jwtToken = requestTokenHeader.substring(7);
        if (isJwtToken(jwtToken)) {
            try {
                username = jwtService.extractUsername(jwtToken);
                log.info("Username : {} ", username);
            } catch (IllegalArgumentException e) {
                log.error("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                log.error("JWT Token has expired");
            }
        } else {
            filterChain.doFilter(request, response);
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            log.info("User details loaded: {}", userDetails.getUsername());

            if (jwtService.validateToken(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                log.info("Authentication set in security context : {} ", authentication);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                log.info("Invalid JWT Token");
            }
        }
        filterChain.doFilter(request, response);
    }



private boolean isJwtToken(String token) {
    try {
        // Attempt to parse the token
        jwtService.extractUsername(token);
        return true;
    } catch (Exception e) {
        return false;
    }
}
}
