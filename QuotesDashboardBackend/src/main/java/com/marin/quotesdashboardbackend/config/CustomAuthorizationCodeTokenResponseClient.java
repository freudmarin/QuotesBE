package com.marin.quotesdashboardbackend.config;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class CustomAuthorizationCodeTokenResponseClient implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {

    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest) {
        RestTemplate restTemplate = new RestTemplate();
        String tokenUri = authorizationGrantRequest.getClientRegistration().getProviderDetails().getTokenUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add(OAuth2ParameterNames.GRANT_TYPE, authorizationGrantRequest.getGrantType().getValue());
        formData.add(OAuth2ParameterNames.CODE, authorizationGrantRequest.getAuthorizationExchange().getAuthorizationResponse().getCode());
        formData.add(OAuth2ParameterNames.REDIRECT_URI, authorizationGrantRequest.getAuthorizationExchange().getAuthorizationRequest().getRedirectUri());
        formData.add(OAuth2ParameterNames.CLIENT_ID, authorizationGrantRequest.getClientRegistration().getClientId());
        formData.add(OAuth2ParameterNames.CLIENT_SECRET, authorizationGrantRequest.getClientRegistration().getClientSecret());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

        Map<String, Object> response = restTemplate.postForObject(tokenUri, request, Map.class);

        return OAuth2AccessTokenResponse.withToken((String) response.get("access_token"))
                .tokenType(OAuth2AccessToken.TokenType.BEARER)
                .expiresIn((Integer) response.get("expires_in"))
                .scopes(authorizationGrantRequest.getClientRegistration().getScopes())
                .refreshToken(response.containsKey("refresh_token") ? (String) response.get("refresh_token") : null)
                .build();
    }
}
