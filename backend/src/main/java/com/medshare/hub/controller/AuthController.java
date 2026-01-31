package com.medshare.hub.controller;

import com.medshare.hub.dto.LoginRequest;
import com.medshare.hub.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AuthController - Authentication endpoints
 * 
 * Endpoints:
 * - POST /api/auth/login - User login
 * - POST /api/auth/refresh - Refresh access token
 * - POST /api/auth/logout - Logout
 * 
 * @author MedShare Development Team
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:3000" })
public class AuthController {

    private final AuthenticationService authenticationService;

    /**
     * Login endpoint
     * Returns access token and refresh token
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for user: {}", request.getEmail());

        Map<String, String> tokens = authenticationService.login(
                request.getEmail(),
                request.getPassword());

        return ResponseEntity.ok(tokens);
    }

    /**
     * Refresh access token using refresh token
     */
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(
            @RequestHeader("Authorization") String refreshToken) {
        // Remove "Bearer " prefix if present
        String token = refreshToken.startsWith("Bearer ") ? refreshToken.substring(7) : refreshToken;

        String newAccessToken = authenticationService.refreshAccessToken(token);

        return ResponseEntity.ok(Map.of(
                "accessToken", newAccessToken,
                "tokenType", "Bearer"));
    }

    /**
     * Logout endpoint
     * 
     * Note: With JWT, logout is handled client-side by deleting the tokens.
     * For additional security, implement token blacklisting with Redis.
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        log.info("User logged out");
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
}
