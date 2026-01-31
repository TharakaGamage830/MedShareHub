package com.medshare.hub.controller;

import com.medshare.hub.dto.LoginRequest;
import com.medshare.hub.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthController authController;

    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        loginRequest = new LoginRequest();
        loginRequest.setEmail("doctor@medshare.com");
        loginRequest.setPassword("password123");
    }

    @Test
    void testLogin_ValidCredentials_ShouldReturnTokens() {
        // Arrange
        Map<String, String> tokens = Map.of(
                "accessToken", "access_jwt",
                "refreshToken", "refresh_jwt");
        when(authenticationService.login("doctor@medshare.com", "password123"))
                .thenReturn(tokens);

        // Act
        ResponseEntity<Map<String, String>> response = authController.login(loginRequest);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertEquals("access_jwt", response.getBody().get("accessToken"));
        assertEquals("refresh_jwt", response.getBody().get("refreshToken"));
    }

    @Test
    void testRefreshToken_ValidToken_ShouldReturnNewAccessToken() {
        // Arrange
        when(authenticationService.refreshAccessToken("valid_refresh_token"))
                .thenReturn("new_access_token");

        // Act
        ResponseEntity<Map<String, String>> response = authController.refreshToken("Bearer valid_refresh_token");

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertEquals("new_access_token", response.getBody().get("accessToken"));
    }

    @Test
    void testMfaVerify_ValidCode_ShouldReturnTokens() {
        // Arrange
        Map<String, String> requestBody = Map.of(
                "mfaToken", "mfa_jwt",
                "code", "123456");
        Map<String, String> tokens = Map.of(
                "accessToken", "final_access_jwt",
                "refreshToken", "final_refresh_jwt");
        when(authenticationService.verifyMfa("mfa_jwt", "123456"))
                .thenReturn(tokens);

        // Act
        ResponseEntity<Map<String, String>> response = authController.verifyMfa(requestBody);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertEquals("final_access_jwt", response.getBody().get("accessToken"));
    }
}
