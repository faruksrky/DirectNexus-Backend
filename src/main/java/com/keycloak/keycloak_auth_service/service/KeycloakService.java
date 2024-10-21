package com.keycloak.keycloak_auth_service.service;


import com.keycloak.keycloak_auth_service.dto.response.TokenDto;

public interface KeycloakService {

    TokenDto getToken (String username, String password);
    TokenDto getTokenAdmin (String username, String password);
    String refreshToken (String refreshToken);
}