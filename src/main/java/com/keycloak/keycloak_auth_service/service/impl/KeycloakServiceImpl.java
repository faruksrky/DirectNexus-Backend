package com.keycloak.keycloak_auth_service.service.impl;

import com.keycloak.keycloak_auth_service.dto.response.TokenDto;
import com.keycloak.keycloak_auth_service.exception.ControllerAdviceException;
import com.keycloak.keycloak_auth_service.exception.CustomException;
import com.keycloak.keycloak_auth_service.service.KeycloakService;
import jakarta.ws.rs.WebApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KeycloakServiceImpl implements KeycloakService {

    @Value("${app.keycloak.serverUrl}")
    private String keycloakServerUrl;

    @Value("${app.keycloak.realm}")
    private String realm;

    @Value("${app.keycloak.admin.clientId}")
    private String clientId;

    @Value("${app.keycloak.admin.clientSecret}")
    private String clientSecret;

    @Value("${app.keycloak.admin.adminClientId}")
    private String adminClientId;

    @Value("${app.keycloak.admin.adminClientSecret}")
    private String adminClientSecret;

    @Override
    public TokenDto getToken(String username, String password) {
        RestTemplate restTemplate = new RestTemplate();
        String url = keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "password");
        map.add("client_id", clientId);
        map.add("username", username);
        map.add("password", password);
        map.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        try {
            ResponseEntity<TokenDto> response = restTemplate.postForEntity(url, request, TokenDto.class);

            if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new CustomException("Geçersiz bir kullanıcı adı veya şifre girdiniz.");
            } else if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new CustomException("Geçersiz bir istek yaptınız.");
            } else if (response.getBody() == null) {
                throw new CustomException("Token alırken bir hata oluştu.");
            }

            return response.getBody();

        } catch (HttpClientErrorException.Unauthorized ex) {
            throw new CustomException("Geçersiz bir kullanıcı adı veya şifre girdiniz");
        } catch (Exception e) {
            throw new CustomException("Beklenmeyen bir hata oluştu: " + e.getMessage(), e);
        }
    }

    @Override
    public String refreshToken (String refreshToken) {
        return null;
    }

    @Override
    public TokenDto getTokenAdmin(String username, String password) {
        RestTemplate restTemplate = new RestTemplate();
        String url = keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "password");
        map.add("client_id", adminClientId);
        map.add("username", username);
        map.add("password", password);
        map.add("client_secret", adminClientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<TokenDto> response = restTemplate.postForEntity(url, request, TokenDto.class);

        return response.getBody();
    }
}