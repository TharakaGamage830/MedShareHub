package com.medshare.hub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Patient Entity - Individual patients whose medical records are managed
 * 
 * Contains patient demographics and Medical Record Number (MRN) for
 * identification.
 * Links to User entity if patient has portal access.
 * 
 * @author MedShare Development Team
 */
@Entity
@Table(name = "patients")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id")
    private Long patientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, unique = true, length = 50)
    private String mrn;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(length = 20)
    private String gender;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(length = 20)
    private String phone;

    @Column(length = 255)
    private String email;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Get full name for display purposes
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Calculate patient age based on date of birth
     * Useful for age-based ABAC policies
     */
    public int getAge() {
        if (dateOfBirth == null)
            return 0;
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }
}
