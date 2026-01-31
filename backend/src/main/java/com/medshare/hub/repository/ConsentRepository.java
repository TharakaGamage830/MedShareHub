package com.medshare.hub.repository;

import com.medshare.hub.entity.Consent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ConsentRepository - Data access for Patient Consents
 * 
 * Used in ABAC policies (e.g., InsuranceClaimsPolicy) to check patient consent
 * before granting access to data for specific purposes.
 * 
 * @author MedShare Development Team
 */
@Repository
public interface ConsentRepository extends JpaRepository<Consent, Long> {

    /**
     * Find valid consents for a patient granted to a specific user
     * Used in ABAC policy evaluation
     */
    @Query("SELECT c FROM Consent c WHERE " +
            "c.patient.patientId = :patientId AND " +
            "c.grantedToUser.userId = :userId AND " +
            "c.revoked = false AND " +
            "(c.expiresAt IS NULL OR c.expiresAt > CURRENT_TIMESTAMP)")
    List<Consent> findValidConsentsForUser(
            @Param("patientId") Long patientId,
            @Param("userId") Long userId);

    /**
     * Check if valid consent exists for specific purpose and data type
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM Consent c WHERE " +
            "c.patient.patientId = :patientId AND " +
            "c.grantedToUser.userId = :userId AND " +
            "c.purpose = :purpose AND " +
            "(c.dataType = 'ALL' OR c.dataType = :dataType) AND " +
            "c.revoked = false AND " +
            "(c.expiresAt IS NULL OR c.expiresAt > CURRENT_TIMESTAMP)")
    boolean hasValidConsent(
            @Param("patientId") Long patientId,
            @Param("userId") Long userId,
            @Param("purpose") Consent.Purpose purpose,
            @Param("dataType") Consent.DataType dataType);

    /**
     * Find all active consents for a patient
     */
    @Query("SELECT c FROM Consent c WHERE " +
            "c.patient.patientId = :patientId AND " +
            "c.revoked = false AND " +
            "(c.expiresAt IS NULL OR c.expiresAt > CURRENT_TIMESTAMP)")
    List<Consent> findActiveConsentsByPatient(@Param("patientId") Long patientId);

    /**
     * Find consents granted to organization (for insurance, etc.)
     */
    @Query("SELECT c FROM Consent c WHERE " +
            "c.patient.patientId = :patientId AND " +
            "c.grantedToOrganization = :organization AND " +
            "c.revoked = false AND " +
            "(c.expiresAt IS NULL OR c.expiresAt > CURRENT_TIMESTAMP)")
    List<Consent> findConsentsByOrganization(
            @Param("patientId") Long patientId,
            @Param("organization") String organization);

    /**
     * Find all consents (including revoked) for patient - for audit purposes
     */
    List<Consent> findByPatient_PatientIdOrderByGrantedAtDesc(Long patientId);
}
