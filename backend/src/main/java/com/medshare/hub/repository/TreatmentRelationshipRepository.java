package com.medshare.hub.repository;

import com.medshare.hub.entity.TreatmentRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * TreatmentRelationshipRepository - Data access for Treatment Relationships
 * 
 * Critical for ABAC TreatingPhysicianPolicy evaluation.
 * Provides queries to check active provider-patient relationships.
 * 
 * @author MedShare Development Team
 */
@Repository
public interface TreatmentRelationshipRepository extends JpaRepository<TreatmentRelationship, Long> {

    /**
     * Find active relationship between provider and patient
     * Used in TreatingPhysicianPolicy to authorize access
     */
    @Query("SELECT tr FROM TreatmentRelationship tr WHERE " +
            "tr.provider.userId = :providerId AND " +
            "tr.patient.patientId = :patientId AND " +
            "tr.status = 'ACTIVE' AND " +
            "(tr.endDate IS NULL OR tr.endDate > CURRENT_TIMESTAMP)")
    Optional<TreatmentRelationship> findActiveRelationship(
            @Param("providerId") Long providerId,
            @Param("patientId") Long patientId);

    /**
     * Check if active relationship exists (boolean check for ABAC)
     */
    @Query("SELECT CASE WHEN COUNT(tr) > 0 THEN true ELSE false END " +
            "FROM TreatmentRelationship tr WHERE " +
            "tr.provider.userId = :providerId AND " +
            "tr.patient.patientId = :patientId AND " +
            "tr.status = 'ACTIVE' AND " +
            "(tr.endDate IS NULL OR tr.endDate > CURRENT_TIMESTAMP)")
    boolean hasActiveRelationship(
            @Param("providerId") Long providerId,
            @Param("patientId") Long patientId);

    /**
     * Find all active relationships for a provider
     */
    @Query("SELECT tr FROM TreatmentRelationship tr WHERE " +
            "tr.provider.userId = :providerId AND " +
            "tr.status = 'ACTIVE' AND " +
            "(tr.endDate IS NULL OR tr.endDate > CURRENT_TIMESTAMP)")
    List<TreatmentRelationship> findActiveRelationshipsByProvider(@Param("providerId") Long providerId);

    /**
     * Find all active relationships for a patient
     */
    @Query("SELECT tr FROM TreatmentRelationship tr WHERE " +
            "tr.patient.patientId = :patientId AND " +
            "tr.status = 'ACTIVE' AND " +
            "(tr.endDate IS NULL OR tr.endDate > CURRENT_TIMESTAMP)")
    List<TreatmentRelationship> findActiveRelationshipsByPatient(@Param("patientId") Long patientId);

    /**
     * Find active relationships of specific type
     */
    @Query("SELECT tr FROM TreatmentRelationship tr WHERE " +
            "tr.provider.userId = :providerId AND " +
            "tr.patient.patientId = :patientId AND " +
            "tr.relationshipType = :type AND " +
            "tr.status = 'ACTIVE'")
    Optional<TreatmentRelationship> findActiveRelationshipByType(
            @Param("providerId") Long providerId,
            @Param("patientId") Long patientId,
            @Param("type") TreatmentRelationship.RelationshipType type);
}
