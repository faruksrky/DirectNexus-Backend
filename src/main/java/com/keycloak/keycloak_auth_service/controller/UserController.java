package com.keycloak.keycloak_auth_service.controller;

import com.keycloak.keycloak_auth_service.dto.request.UserRequest;
import com.keycloak.keycloak_auth_service.dto.response.UserResponse;
import com.keycloak.keycloak_auth_service.entity.NewUserRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.keycloak.keycloak_auth_service.service.UserService;

import java.util.List;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserRequest userRequest) {
        userService.createUser(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}/send-verification-email")
    public ResponseEntity<?> sendVerificationEmail(@PathVariable String id) {

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {

        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @PutMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String username) {

        userService.forgotPassword(username);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

        @GetMapping("/userInfo")
    public ResponseEntity<?> getUserInfoByToken(@RequestBody String accessToken) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserInfoByToken(accessToken));
    }

    @GetMapping("/usernames")
    public ResponseEntity<List<String>> getAllUsernames() {
        List<String> usernames = userService.getAllUsernames();
        return ResponseEntity.ok(usernames);
    }
}
