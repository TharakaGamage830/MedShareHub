package com.medshare.hub.service;

import com.medshare.hub.entity.User;
import com.medshare.hub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * UserService - User management operations
 * 
 * Handles:
 * - User creation and registration
 * - User attribute retrieval for ABAC
 * - Profile updates
 * - User search
 * 
 * @author MedShare Development Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder(12);
    }

    /**
     * Create new user (registration)
     */
    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public User createUser(User user, String plainPassword) {
        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + user.getEmail());
        }

        // Hash password
        user.setPasswordHash(passwordEncoder.encode(plainPassword));

        User savedUser = userRepository.save(user);
        log.info("Created new user: {} ({})", savedUser.getEmail(), savedUser.getRole());

        return savedUser;
    }

    /**
     * Get user by ID (cached for ABAC performance)
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#userId")
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }

    /**
     * Get user by email
     */
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));
    }

    /**
     * Update user profile
     */
    @Transactional
    @CacheEvict(value = "users", key = "#user.userId")
    public User updateUser(User user) {
        if (!userRepository.existsById(user.getUserId())) {
            throw new IllegalArgumentException("User not found: " + user.getUserId());
        }

        User updated = userRepository.save(user);
        log.info("Updated user: {}", user.getUserId());

        return updated;
    }

    /**
     * Get users by role
     */
    @Transactional(readOnly = true)
    public List<User> getUsersByRole(User.UserRole role) {
        return userRepository.findByRole(role);
    }

    /**
     * Get emergency-certified users
     */
    @Transactional(readOnly = true)
    public List<User> getEmergencyCertifiedUsers() {
        return userRepository.findEmergencyCertifiedUsers();
    }

    /**
     * Change user password
     */
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getUserById(userId);

        // Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Set new password
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("Password changed for user: {}", userId);
    }
}
