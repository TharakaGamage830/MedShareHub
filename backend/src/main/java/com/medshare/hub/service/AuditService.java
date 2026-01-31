package com.medshare.hub.service;

import com.medshare.hub.entity.AccessLog;
import com.medshare.hub.repository.AccessLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AuditService - HIPAA-compliant audit logging
 * 
 * Handles:
 * - Asynchronous audit log creation (non-blocking)
 * - Audit log retrieval and querying
 * - Anomaly detection (simple rules-based)
 * - Compliance reporting
 * 
 * HIPAA Compliance:
 * - All access attempts are logged (permit and deny)
 * - Logs are immutable (enforced by database rules)
 * - 7-year retention policy
 * - Tamper-proof audit trail
 * 
 * Performance:
 * - Async logging prevents impact on policy evaluation time
 * - Target: <5ms overhead for audit logging
 * 
 * @author MedShare Development Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AccessLogRepository accessLogRepository;

    /**
     * Create audit log entry (asynchronous)
     * 
     * Logs every access attempt with full context.
     * Runs asynchronously to not impact application performance.
     * 
     * @param userId        User making the request
     * @param patientId     Patient whose data is being accessed
     * @param resourceType  Type of resource (MEDICAL_RECORD, etc.)
     * @param resourceId    ID of specific resource
     * @param action        Action attempted (READ, WRITE, etc.)
     * @param decision      Policy decision (PERMIT/DENY)
     * @param policyMatched Name of policy that made decision
     * @param denyReason    Reason if denied
     * @param isEmergency   Whether this was emergency access
     * @param justification Justification for emergency access
     * @param ipAddress     IP address of request
     * @param sessionId     Session identifier
     */
    @Async
    @Transactional
    public void logAccess(
            Long userId,
            Long patientId,
            String resourceType,
            Long resourceId,
            AccessLog.Action action,
            AccessLog.Decision decision,
            String policyMatched,
            String denyReason,
            Boolean isEmergency,
            String justification,
            String ipAddress,
            String sessionId) {
        AccessLog log = new AccessLog();
        log.setUser(userId != null ? new com.medshare.hub.entity.User() {
            {
                setUserId(userId);
            }
        } : null);
        log.setPatient(patientId != null ? new com.medshare.hub.entity.Patient() {
            {
                setPatientId(patientId);
            }
        } : null);
        log.setResourceType(resourceType);
        log.setResourceId(resourceId);
        log.setAction(action);
        log.setDecision(decision);
        log.setPolicyMatched(policyMatched);
        log.setDenyReason(denyReason);
        log.setIsEmergency(isEmergency != null && isEmergency);
        log.setJustification(justification);
        log.setIpAddress(ipAddress);
        log.setSessionId(sessionId);

        accessLogRepository.save(log);

        if (isEmergency != null && isEmergency) {
            log.warn("EMERGENCY ACCESS logged: User {} accessed patient {} - Justification: {}",
                    userId, patientId, justification);
        }

        log.debug("Access logged: user={}, patient={}, action={}, decision={}",
                userId, patientId, action, decision);
    }

    /**
     * Get access logs for a specific user (paginated)
     */
    @Transactional(readOnly = true)
    public Page<AccessLog> getUserAccessLogs(Long userId, Pageable pageable) {
        return accessLogRepository.findByUser_UserIdOrderByCreatedAtDesc(userId, pageable);
    }

    /**
     * Get access logs for a specific patient (paginated)
     * Shows who accessed patient's records
     */
    @Transactional(readOnly = true)
    public Page<AccessLog> getPatientAccessLogs(Long patientId, Pageable pageable) {
        return accessLogRepository.findByPatient_PatientIdOrderByCreatedAtDesc(patientId, pageable);
    }

    /**
     * Get recent denied access attempts (security monitoring)
     */
    @Transactional(readOnly = true)
    public List<AccessLog> getRecentDeniedAccesses(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return accessLogRepository.findDeniedAccessesSince(since);
    }

    /**
     * Get all emergency access logs (break-glass auditing)
     */
    @Transactional(readOnly = true)
    public Page<AccessLog> getEmergencyAccesses(Pageable pageable) {
        return accessLogRepository.findEmergencyAccesses(pageable);
    }

    /**
     * Anomaly detection: Check if user has unusual number of denied accesses
     * Simple rule-based detection
     * 
     * @param userId    User to check
     * @param hours     Time window
     * @param threshold Number of denials that indicates anomaly
     * @return true if anomaly detected
     */
    @Transactional(readOnly = true)
    public boolean detectAnomalousActivity(Long userId, int hours, int threshold) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        long deniedCount = accessLogRepository.countDeniedAccessesByUserSince(userId, since);

        if (deniedCount >= threshold) {
            log.warn("ANOMALOUS ACTIVITY DETECTED: User {} has {} denied access attempts in last {} hours",
                    userId, deniedCount, hours);
            return true;
        }

        return false;
    }

    /**
     * Get compliance report for date range
     * Shows all access attempts for compliance auditing
     */
    @Transactional(readOnly = true)
    public Page<AccessLog> getComplianceReport(
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable) {
        return accessLogRepository.findByDateRange(startDate, endDate, pageable);
    }
}
