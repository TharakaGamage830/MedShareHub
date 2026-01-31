package com.medshare.hub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Medical Record Entity - Medical records with sensitivity-based access control
 * 
 * Stores medical information in FHIR R4 compatible JSON format.
 * Sensitivity level is a critical ABAC attribute used in policy evaluation.
 * 
 * Sensitivity Levels:
 * - PUBLIC: Non-sensitive information
 * - STANDARD: Standard medical information
 * - PSYCHIATRIC: Mental health records (restricted)
 * - HIV: HIV-related records (highly restricted)
 * - CRITICAL: Critical sensitive information
 * 
 * @author MedShare Development Team
 */
@Entity
@Table(name = "medical_records")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long recordId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Enumerated(EnumType.STRING)
    @Column(name = "record_type", nullable = false, length = 50)
    private RecordType recordType;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "sensitivity_level", nullable = false, length = 50)
    private SensitivityLevel sensitivityLevel;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Types of medical records
     */
    public enum RecordType {
        LAB_RESULT,
        PRESCRIPTION,
        VISIT_NOTE,
        IMAGING,
        DIAGNOSIS,
        PROCEDURE
    }

    /**
     * Sensitivity levels for ABAC policy evaluation
     * Higher sensitivity requires stricter access control
     */
    public enum SensitivityLevel {
        PUBLIC,
        STANDARD,
        PSYCHIATRIC,
        HIV,
        CRITICAL
    }

    /**
     * Check if record is highly sensitive
     * Used in ABAC policies to enforce stricter access rules
     */
    public boolean isHighlySensitive() {
        return sensitivityLevel == SensitivityLevel.PSYCHIATRIC ||
                sensitivityLevel == SensitivityLevel.HIV ||
                sensitivityLevel == SensitivityLevel.CRITICAL;
    }
}
