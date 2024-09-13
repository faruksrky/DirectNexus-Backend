package com.keycloak.keycloak_auth_service.controller;

import com.keycloak.keycloak_auth_service.service.KeycloakService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/keycloak")
@RequiredArgsConstructor
public class LoginController {

    private final KeycloakService keycloakService;

    @GetMapping ("/admin")
    public ResponseEntity<String> getAdminDetails() {
        return ResponseEntity.ok("Admin Details");
    }

    @GetMapping ("/user")
    public ResponseEntity<?> getUserDetails() {
        return ResponseEntity.ok("User Details");
    }

    @PostMapping("/getToken")
    public ResponseEntity<?> getToken(@RequestParam String username, @RequestParam String password) {
        String token = keycloakService.getToken(username, password);
        return ResponseEntity.ok(token);
    }



}
