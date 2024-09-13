package com.keycloak.keycloak_auth_service.entity;

public record NewUserRecord(
        String userName,
        String password,
        String firstName,
        String lastName,
        String email
) {
}
