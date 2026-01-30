package com.medshare.hub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Access Log Entity - Immutable audit trail for HIPAA compliance
 * 
 * Records every access attempt (permit or deny) for comprehensive auditing.
 * Logs are tamper-proof (no updates/deletes allowed via database rules).
 * 
 * Includes:
 * - Who accessed (user)
 * - What was accessed (resource)
 * - When it was accessed (timestamp)
 * - What action was attempted (READ, WRITE, etc.)
 * - Decision made (PERMIT or DENY)
 * - Which ABAC policy made the decision
 * - Context information (IP, device, session)
 * - Emergency access details if applicable
 * 
 * @author MedShare Development Team
 */
@Entity
@Table(name = "access_logs")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @Column(name = "resource_type", nullable = false, length = 50)
    private String resourceType;

    @Column(name = "resource_id")
    private Long resourceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Action action;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Decision decision;

    @Column(name = "policy_matched", length = 100)
    private String policyMatched;

    @Column(name = "deny_reason", columnDefinition = "TEXT")
    private String denyReason;

    @Column(columnDefinition = "TEXT")
    private String justification;

    @Column(name = "is_emergency")
    private Boolean isEmergency = false;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "device_info", columnDefinition = "TEXT")
    private String deviceInfo;

    @Column(name = "session_id", length = 100)
    private String sessionId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Actions that can be performed on resources
     */
    public enum Action {
        READ,
        WRITE,
        UPDATE,
        DELETE,
        EXPORT,
        PRINT
    }

    /**
     * ABAC policy decision
     */
    public enum Decision {
        PERMIT,
        DENY
    }

    /**
     * Check if this was an emergency (break-glass) access
     */
    public boolean isBreakGlassAccess() {
        return isEmergency != null && isEmergency;
    }

    /**
     * Check if access was denied
     */
    public boolean wasDenied() {
        return decision == Decision.DENY;
    }
}
