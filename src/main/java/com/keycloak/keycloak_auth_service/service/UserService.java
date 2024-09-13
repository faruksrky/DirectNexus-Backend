package com.keycloak.keycloak_auth_service.service;

import com.keycloak.keycloak_auth_service.entity.NewUserRecord;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;

import java.util.List;

public interface UserService {
    void createUser(NewUserRecord newUserRecord);
    void deleteUser(String userId);
    void forgotPassword(String username);
    UserResource getUser(String userId);
    List<RoleRepresentation> getUserRoles(String userId);
    List<GroupRepresentation> getUserGroups(String userId);

}
