package com.keycloak.keycloak_auth_service.service.impl;

import com.keycloak.keycloak_auth_service.dto.request.UserRequest;
import com.keycloak.keycloak_auth_service.dto.response.UserResponse;
import com.keycloak.keycloak_auth_service.service.UserService;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Value("${app.keycloak.realm}")
    private String realm;

    @Value("${app.keycloak.admin.adminClientSecret}")
    private String adminClientSecret;

    private final Keycloak keycloak;

    @Override
    public void createUser(UserRequest userRequest) {
        if (userRequest.getUserName().isEmpty() ||
                userRequest.getEmail().isEmpty() ||
                userRequest.getPassword().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kullanıcı adı, şifre ve email boş olamaz");
        }

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEnabled(true);
        userRepresentation.setFirstName(userRequest.getFirstName());
        userRepresentation.setLastName(userRequest.getLastName());
        userRepresentation.setUsername(userRequest.getUserName());
        userRepresentation.setEmail(userRequest.getEmail());
        userRepresentation.setEmailVerified(false);

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setValue(userRequest.getPassword());
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);

        userRepresentation.setCredentials(List.of(credentialRepresentation));

        UsersResource usersResource = getUsersResource();
        if (!CollectionUtils.isEmpty(usersResource.list()) &&
                usersResource.list().stream().anyMatch(x -> x.getUsername().equals(userRequest.getUserName()))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Kullanıcı zaten mevcut");
        }

        try (Response response = usersResource.create(userRepresentation)) {
            if (!Objects.equals(201, response.getStatus())) {
                throw new RuntimeException("Status code " + response.getStatus());
            }
        }

        List<UserRepresentation> userRepresentations = usersResource.searchByUsername(userRequest.getUserName(), true);
        UserRepresentation userRepresentation1 = userRepresentations.get(0);
    }

    @Override
    public void deleteUser(String userId) {
        UsersResource usersResource = getUsersResource();
        usersResource.delete(userId);
    }

    @Override
    public void forgotPassword(String username) {
        UsersResource usersResource = getUsersResource();
        List<UserRepresentation> userRepresentations = usersResource.searchByUsername(username, true);
        UserRepresentation userRepresentation1 = userRepresentations.get(0);
        UserResource userResource = usersResource.get(userRepresentation1.getId());
        userResource.executeActionsEmail(List.of("UPDATE_PASSWORD"));
    }

    @Override
    public List<String> getAllUsernames() {
        try {
            UsersResource usersResource = getUsersResource();
            List<UserRepresentation> userRepresentations = usersResource.list();

            if (userRepresentations == null || userRepresentations.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No users found");
            }

            return userRepresentations.stream()
                    .map(UserRepresentation::getUsername)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while fetching usernames", e);
        }
    }

    private UsersResource getUsersResource() {
        return keycloak.realm(realm).users();
    }


    @Override
    public List<UserResponse> getAllUsers() {
        try {
            UsersResource usersResource = getUsersResource();
            List<UserRepresentation> userRepresentations = usersResource.list();

            if (userRepresentations == null || userRepresentations.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Kullanıcı bulunamadı");
            }

            return userRepresentations.stream()
                    .map(user -> {
                        UserResponse userResponse = new UserResponse();
                        userResponse.setId(user.getId());
                        userResponse.setUserName(user.getUsername());
                        userResponse.setFirstName(user.getFirstName());
                        userResponse.setLastName(user.getLastName());
                        userResponse.setEmail(user.getEmail());
                        return userResponse;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Kullanıcı listesi çekilirken bir hata oluştu", e);
        }
    }
    @Override
    public UserRepresentation getUserInfoByToken(String accessToken) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(adminClientSecret.getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();

            UserRepresentation userRepresentation = new UserRepresentation();
            userRepresentation.setId(claims.getSubject());
            userRepresentation.setUsername(claims.get("preferred_username", String.class));
            userRepresentation.setEmail(claims.get("email", String.class));
            userRepresentation.setFirstName(claims.get("given_name", String.class));
            userRepresentation.setLastName(claims.get("family_name", String.class));

            return userRepresentation;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while parsing the token", e);
        }
    }
}