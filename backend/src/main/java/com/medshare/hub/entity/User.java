package com.medshare.hub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * User Entity - Healthcare providers, patients, administrators, and other
 * system users
 * 
 * Stores ABAC (Attribute-Based Access Control) attributes used for
 * authorization:
 * - role: User's primary role in the system
 * - department: Department affiliation for department-based access control
 * - certifications: Professional certifications affecting access rights
 * - emergencyCertified: Whether user can perform break-glass emergency access
 * 
 * @author MedShare Development Team
 */
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private UserRole role;

    @Column(length = 100)
    private String department;

    @Column(columnDefinition = "text[]")
    private String[] certifications;

    @Column(length = 200)
    private String employer;

    @Column(length = 200)
    private String location;

    @Column(name = "emergency_certified")
    private Boolean emergencyCertified = false;

    @Column(name = "mfa_enabled")
    private Boolean mfaEnabled = false;

    @Column(name = "mfa_secret")
    private String mfaSecret;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * User roles for ABAC authorization
     */
    public enum UserRole {
        DOCTOR,
        PATIENT,
        PHARMACIST,
        ADMIN,
        INSURANCE_ADJUSTER
    }

    /**
     * Get full name for display purposes
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Check if user has a specific certification
     * Used in ABAC policy evaluation
     */
    public boolean hasCertification(String certification) {
        if (certifications == null)
            return false;
        for (String cert : certifications) {
            if (cert.equalsIgnoreCase(certification)) {
                return true;
            }
        }
        return false;
    }
}
