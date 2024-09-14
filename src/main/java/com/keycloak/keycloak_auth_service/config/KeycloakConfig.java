package com.keycloak.keycloak_auth_service.config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

    @Value("${app.keycloak.admin.clientId}")
    private String clientId;

    @Value("${app.keycloak.realm}")
    private String realm;

    @Value("${app.keycloak.serverUrl}")
    private String serverUrl;

    @Value("${app.keycloak.admin.clientSecret}")
    private String clientSecret;

    @Value("${app.keycloak.adminClientId}")
    private String adminClientId;


    @Value("${app.keycloak.adminClientSecret}")
    private String adminClientSecret;


    @Bean
    public Keycloak keycloak(){
        return KeycloakBuilder.builder()
                .clientSecret(adminClientSecret)
                .serverUrl(serverUrl)
                .clientId(adminClientId)
                .realm(realm)
                .grantType("client_credentials")
                .build();
    }
}
