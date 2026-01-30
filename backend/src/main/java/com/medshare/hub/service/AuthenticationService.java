package com.medshare.hub.service;

import com.medshare.hub.entity.User;
import com.medshare.hub.repository.UserRepository;
import com.medshare.hub.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * AuthenticationService - User authentication and JWT token management
 * 
 * Handles:
 * - User login with email/password
 * - JWT token generation
 * - Password validation using BCrypt
 * - Refresh token management
 * 
 * Security:
 * - Passwords stored as BCrypt hashes (strength 12)
 * - Failed authentication attempts are logged
 * - Account lockout can be implemented for repeated failures
 * 
 * @author MedShare Development Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Default constructor provides BCryptPasswordEncoder
     */
    public AuthenticationService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = new BCryptPasswordEncoder(12); // Strength 12 for security
    }

    /**
     * Authenticate user and generate JWT tokens
     * 
     * @param email    User email
     * @param password Plain text password
     * @return Map containing accessToken and refreshToken
     * @throws BadCredentialsException if authentication fails
     */
    @Transactional(readOnly = true)
    public Map<String, String> login(String email, String password) {
        log.debug("Authentication attempt for user: {}", email);

        // Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Login failed: User not found - {}", email);
                    return new BadCredentialsException("Invalid email or password");
                });

        // Validate password
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            log.warn("Login failed: Invalid password for user - {}", email);
            throw new BadCredentialsException("Invalid email or password");
        }

        // Generate tokens
        String accessToken = jwtTokenProvider.generateToken(email);
        String refreshToken = jwtTokenProvider.generateRefreshToken(email);

        log.info("User authenticated successfully: {} (role: {})", email, user.getRole());

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        tokens.put("tokenType", "Bearer");
        tokens.put("userId", user.getUserId().toString());
        tokens.put("role", user.getRole().toString());

        return tokens;
    }

    /**
     * Refresh access token using refresh token
     * 
     * @param refreshToken Valid refresh token
     * @return New access token
     */
    public String refreshAccessToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BadCredentialsException("Invalid or expired refresh token");
        }

        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        return jwtTokenProvider.generateToken(username);
    }

    /**
     * Hash password for storage
     * Used during user registration
     */
    public String hashPassword(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }

    /**
     * Validate password strength
     * Minimum 12 characters, complexity requirements
     */
    public boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 12) {
            return false;
        }

        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasLowercase = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");

        return hasUppercase && hasLowercase && hasDigit && hasSpecial;
    }
}
