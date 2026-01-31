package com.medshare.hub.repository;

import com.medshare.hub.entity.MedicalRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MedicalRecordRepository - Data access for Medical Record entities
 * 
 * Provides paginated queries with sensitivity-based filtering for ABAC.
 * All queries should be checked against ABAC policies before returning data.
 * 
 * @author MedShare Development Team
 */
@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    /**
     * Find all medical records for a patient (paginated)
     * ABAC policy check required before calling
     */
    Page<MedicalRecord> findByPatient_PatientId(Long patientId, Pageable pageable);

    /**
     * Find medical records by patient and sensitivity level
     * Used for filtering sensitive records in ABAC policies
     */
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.patient.patientId = :patientId " +
            "AND mr.sensitivityLevel = :sensitivityLevel " +
            "ORDER BY mr.createdAt DESC")
    List<MedicalRecord> findByPatientAndSensitivityLevel(
            @Param("patientId") Long patientId,
            @Param("sensitivityLevel") MedicalRecord.SensitivityLevel sensitivityLevel);

    /**
     * Find medical records by patient and record type (paginated)
     */
    Page<MedicalRecord> findByPatient_PatientIdAndRecordType(
            Long patientId,
            MedicalRecord.RecordType recordType,
            Pageable pageable);

    /**
     * Find highly sensitive records (PSYCHIATRIC, HIV, CRITICAL)
     * Requires special ABAC authorization
     */
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.patient.patientId = :patientId " +
            "AND mr.sensitivityLevel IN ('PSYCHIATRIC', 'HIV', 'CRITICAL') " +
            "ORDER BY mr.createdAt DESC")
    List<MedicalRecord> findHighlySensitiveRecords(@Param("patientId") Long patientId);

    /**
     * Find records created by a specific provider
     */
    Page<MedicalRecord> findByCreatedBy_UserId(Long userId, Pageable pageable);

    /**
     * Count records by patient
     */
    long countByPatient_PatientId(Long patientId);
}
