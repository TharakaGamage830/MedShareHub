package com.medshare.hub.controller;

import com.medshare.hub.entity.User;
import com.medshare.hub.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * UserController - User profile and management endpoints
 * 
 * Endpoints:
 * - GET /api/users/me - Get current user profile
 * - PUT /api/users/me - Update current user profile
 * - POST /api/users/change-password - Change user password
 * 
 * @author MedShare Development Team
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:3000" })
public class UserController {

    private final UserService userService;

    /**
     * Get current authenticated user profile
     */
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(Authentication authentication) {
        Long userId = extractUserIdFromAuth(authentication);
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    /**
     * Update current user profile
     */
    @PutMapping("/me")
    public ResponseEntity<User> updateProfile(
            @RequestBody User profileUpdate,
            Authentication authentication) {
        Long userId = extractUserIdFromAuth(authentication);
        User existingUser = userService.getUserById(userId);

        // Update allowed fields
        existingUser.setFirstName(profileUpdate.getFirstName());
        existingUser.setLastName(profileUpdate.getLastName());

        User updated = userService.updateUser(existingUser);
        log.info("Profile updated for user: {}", userId);
        return ResponseEntity.ok(updated);
    }

    /**
     * Change password request
     */
    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            Authentication authentication) {
        Long userId = extractUserIdFromAuth(authentication);
        userService.changePassword(userId, oldPassword, newPassword);
        return ResponseEntity.ok().build();
    }

    /**
     * Extract user ID from authentication
     */
    private Long extractUserIdFromAuth(Authentication authentication) {
        if (authentication != null
                && authentication.getPrincipal() instanceof com.medshare.hub.security.CustomUserDetails) {
            return ((com.medshare.hub.security.CustomUserDetails) authentication.getPrincipal()).getUserId();
        }
        throw new RuntimeException("User identity not found in security context");
    }
}
