package com.medshare.hub.service;

import com.medshare.hub.entity.User;
import com.medshare.hub.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Note: UserService uses its own internal BCryptPasswordEncoder sometimes in
        // the constructor
        // if not provided, but in the implementation it has a constructor for
        // injection.

        user = new User();
        user.setUserId(1L);
        user.setEmail("test@medshare.com");
        user.setPasswordHash("hashed_pass");
    }

    @Test
    void testCreateUser_Valid_ShouldSaveUser() {
        // Arrange
        when(userRepository.existsByEmail("test@medshare.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("hashed_pass");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User created = userService.createUser(user, "password");

        // Assert
        assertNotNull(created);
        assertEquals("test@medshare.com", created.getEmail());
        verify(userRepository).save(user);
    }

    @Test
    void testCreateUser_DuplicateEmail_ShouldThrowException() {
        // Arrange
        when(userRepository.existsByEmail("test@medshare.com")).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user, "password"));
    }

    @Test
    void testGetUserById_Found_ShouldReturnUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        User result = userService.getUserById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
    }

    @Test
    void testGetUserById_NotFound_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.getUserById(1L));
    }

    @Test
    void testChangePassword_CorrectOldPassword_ShouldUpdate() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old_pass", "hashed_pass")).thenReturn(true);
        when(passwordEncoder.encode("new_pass")).thenReturn("new_hashed_pass");

        // Act
        userService.changePassword(1L, "old_pass", "new_pass");

        // Assert
        assertEquals("new_hashed_pass", user.getPasswordHash());
        verify(userRepository).save(user);
    }

    @Test
    void testChangePassword_IncorrectOldPassword_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong_pass", "hashed_pass")).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.changePassword(1L, "wrong_pass", "new_pass"));
    }
}
