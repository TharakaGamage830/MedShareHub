package com.medshare.hub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Consent Entity - Patient consent for data sharing
 * 
 * Implements granular patient consent management for ABAC policies.
 * Patients can grant/revoke consent for specific data types and purposes.
 * 
 * Consent is checked in ABAC policies like InsuranceClaimsPolicy before
 * granting access to patient data.
 * 
 * @author MedShare Development Team
 */
@Entity
@Table(name = "consents")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Consent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "consent_id")
    private Long consentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "granted_to_user_id")
    private User grantedToUser;

    @Column(name = "granted_to_organization", length = 200)
    private String grantedToOrganization;

    @Enumerated(EnumType.STRING)
    @Column(name = "data_type", length = 50)
    private DataType dataType;

    @Enumerated(EnumType.STRING)
    @Column(length = 100)
    private Purpose purpose;

    @CreatedDate
    @Column(name = "granted_at", nullable = false, updatable = false)
    private LocalDateTime grantedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private Boolean revoked = false;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    /**
     * Granular data types for consent
     */
    public enum DataType {
        ALL,
        LAB_RESULTS,
        PRESCRIPTIONS,
        VISIT_NOTES,
        IMAGING,
        BILLING
    }

    /**
     * Purpose of data access
     */
    public enum Purpose {
        TREATMENT,
        INSURANCE,
        RESEARCH,
        FAMILY_ACCESS,
        OTHER
    }

    /**
     * Check if consent is currently valid
     * Used in ABAC policy evaluation
     */
    public boolean isValid() {
        if (revoked) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        return expiresAt == null || now.isBefore(expiresAt);
    }

    /**
     * Revoke this consent
     * Patient can revoke consent at any time
     */
    public void revoke() {
        this.revoked = true;
        this.revokedAt = LocalDateTime.now();
    }

    /**
     * Check if consent covers specific data type
     */
    public boolean coversDataType(DataType requestedType) {
        return this.dataType == DataType.ALL || this.dataType == requestedType;
    }
}
