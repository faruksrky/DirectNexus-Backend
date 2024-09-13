package com.keycloak.keycloak_auth_service.service;


public interface KeycloakService {

    String getToken (String username, String password);
    String refreshToken (String refreshToken);
}
