package com.jawad.bank.account.services;

import com.jawad.bank.account.dtos.LoginRequest;
import com.jawad.bank.account.dtos.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final RestClient restClient;

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.public-client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    public LoginResponse login(LoginRequest request) {

        String body = "grant_type=password"
                + "&client_id=" + encode(clientId)
                + "&client_secret=" + encode(clientSecret)
                + "&username=" + encode(request.getUsername())
                + "&password=" + encode(request.getPassword());

        Map<String, Object> token = restClient.post()
                .uri(serverUrl + "/realms/" + realm + "/protocol/openid-connect/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(Map.class);

        LoginResponse response = new LoginResponse();
        response.setAccessToken((String) token.get("access_token"));
        response.setRefreshToken((String) token.get("refresh_token"));
        response.setExpiresIn(((Number) token.get("expires_in")).longValue());
        response.setTokenType((String) token.get("token_type"));

        return response;
    }

    private String encode(String value) {
        return UriUtils.encode(value, StandardCharsets.UTF_8);
    }
}