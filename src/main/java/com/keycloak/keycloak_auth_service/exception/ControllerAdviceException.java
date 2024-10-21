package com.keycloak.keycloak_auth_service.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@AllArgsConstructor
@RestControllerAdvice
public class ControllerAdviceException extends ResponseEntityExceptionHandler {

    @ExceptionHandler({HttpClientErrorException.Unauthorized.class})
    public final ResponseEntity<String> handleException(HttpClientErrorException.Unauthorized ex) {
        return ResponseEntity.status (HttpStatus.UNAUTHORIZED)
                             .body("Yetkisiz erişim. Lütfen geçerli kullanıcı adı ve şifre ile giriş yapın.");
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<String> handleCustomException(CustomException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Bir hata oluştu: " + ex.getMessage());
    }
}
