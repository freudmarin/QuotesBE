package com.marin.quotesdashboardbackend.config;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomGoogleTokenIntrospector implements OpaqueTokenIntrospector {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String introspectionUri = "https://oauth2.googleapis.com/tokeninfo";

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromHttpUrl(introspectionUri)
                .queryParam("access_token", token);

        Map<String, Object> response;
        try {
            response = restTemplate.getForObject(uriBuilder.toUriString(), Map.class);
        } catch (Exception ex) {
            throw new OAuth2AuthenticationException("Invalid token : " + ex);
        }

        if (response.containsKey("error")) {
            throw new OAuth2AuthenticationException("Invalid token");
        }

        // Convert relevant fields to Instant
        Map<String, Object> claims = new HashMap<>(response);
        if (response.containsKey("exp")) {
            claims.put("exp", Instant.ofEpochSecond(Long.parseLong((String) response.get("exp"))));
        }
        if (response.containsKey("iat")) {
            claims.put("iat", Instant.ofEpochSecond(Long.parseLong((String) response.get("iat"))));
        }

        return new OAuth2IntrospectionAuthenticatedPrincipal(
                claims,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
