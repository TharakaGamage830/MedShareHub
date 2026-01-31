package com.medshare.hub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Treatment Relationship Entity - Provider-patient treatment relationships
 * 
 * Critical ABAC attribute for TreatingPhysicianPolicy.
 * Only providers with ACTIVE treatment relationships can access patient records
 * (subject to other ABAC conditions like sensitivity levels and business
 * hours).
 * 
 * Relationship Types:
 * - TREATING: Primary physician (ongoing care)
 * - CONSULTING: Specialist consultation (temporary)
 * - EMERGENCY: Emergency department physician (temporary)
 * 
 * @author MedShare Development Team
 */
@Entity
@Table(name = "treatment_relationships")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreatmentRelationship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "relationship_id")
    private Long relationshipId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "provider_id", nullable = false)
    private User provider;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Enumerated(EnumType.STRING)
    @Column(name = "relationship_type", nullable = false, length = 50)
    private RelationshipType relationshipType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.ACTIVE;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Types of provider-patient relationships
     */
    public enum RelationshipType {
        TREATING, // Primary physician
        CONSULTING, // Specialist consultation
        EMERGENCY // Emergency department
    }

    /**
     * Relationship status for access control
     */
    public enum Status {
        ACTIVE,
        ENDED
    }

    /**
     * Check if relationship is currently active
     * Used in ABAC TreatingPhysicianPolicy evaluation
     */
    public boolean isActive() {
        if (status != Status.ACTIVE) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        return (endDate == null || now.isBefore(endDate)) && now.isAfter(startDate);
    }

    /**
     * End the treatment relationship
     * Updates status and sets end date
     */
    public void endRelationship() {
        this.status = Status.ENDED;
        this.endDate = LocalDateTime.now();
    }
}
