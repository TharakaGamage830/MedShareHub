package com.medshare.hub.repository;

import com.medshare.hub.entity.AccessLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AccessLogRepository - Data access for Access Logs (Audit Trail)
 * 
 * Provides queries for HIPAA-compliant audit log retrieval and analysis.
 * Note: Logs are immutable (no updates/deletes allowed by database rules).
 * 
 * @author MedShare Development Team
 */
@Repository
public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {

    /**
     * Find access logs by user (paginated)
     * For user activity auditing
     */
    Page<AccessLog> findByUser_UserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * Find access logs by patient (paginated)
     * For patient record access transparency
     */
    Page<AccessLog> findByPatient_PatientIdOrderByCreatedAtDesc(Long patientId, Pageable pageable);

    /**
     * Find denied access attempts (security monitoring)
     */
    @Query("SELECT al FROM AccessLog al WHERE " +
            "al.decision = 'DENY' AND " +
            "al.createdAt >= :since " +
            "ORDER BY al.createdAt DESC")
    List<AccessLog> findDeniedAccessesSince(@Param("since") LocalDateTime since);

    /**
     * Find emergency access logs (break-glass auditing)
     */
    @Query("SELECT al FROM AccessLog al WHERE " +
            "al.isEmergency = true " +
            "ORDER BY al.createdAt DESC")
    Page<AccessLog> findEmergencyAccesses(Pageable pageable);

    /**
     * Find access logs by user and patient (relationship auditing)
     */
    @Query("SELECT al FROM AccessLog al WHERE " +
            "al.user.userId = :userId AND " +
            "al.patient.patientId = :patientId " +
            "ORDER BY al.createdAt DESC")
    List<AccessLog> findByUserAndPatient(
            @Param("userId") Long userId,
            @Param("patientId") Long patientId);

    /**
     * Find access logs by date range (compliance reporting)
     */
    @Query("SELECT al FROM AccessLog al WHERE " +
            "al.createdAt BETWEEN :startDate AND :endDate " +
            "ORDER BY al.createdAt DESC")
    Page<AccessLog> findByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    /**
     * Count denied accesses for a user (anomaly detection)
     */
    @Query("SELECT COUNT(al) FROM AccessLog al WHERE " +
            "al.user.userId = :userId AND " +
            "al.decision = 'DENY' AND " +
            "al.createdAt >= :since")
    long countDeniedAccessesByUserSince(
            @Param("userId") Long userId,
            @Param("since") LocalDateTime since);

    /**
     * Find logs by action type (e.g., all EXPORT actions)
     */
    Page<AccessLog> findByActionOrderByCreatedAtDesc(AccessLog.Action action, Pageable pageable);
}
