package com.marin.quotesdashboardbackend.config;

import com.marin.quotesdashboardbackend.services.CustomOAuth2UserService;
import com.marin.quotesdashboardbackend.services.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    private final CustomOAuth2UserService customOAuth2UserService;

    private final JwtRequestFilter jwtRequestFilter;

    private final CustomAuthorizationCodeTokenResponseClient customAuthorizationCodeTokenResponseClient;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers( "api/v1/auth/**", "/oauth2/**", "login/oauth2/code/google").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authz -> authz
                                .baseUri("/oauth2/authorization/google"))
                        .redirectionEndpoint(redir -> redir
                                .baseUri("login/oauth2/code/*"))
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)))
                .oauth2ResourceServer(oauth2 -> oauth2
                                .opaqueToken(opaqueToken -> opaqueToken
                                        .introspector(new CustomGoogleTokenIntrospector()))).addFilterBefore(jwtRequestFilter,
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
