package com.medshare.hub.service;

import com.medshare.hub.entity.AccessLog;
import com.medshare.hub.entity.User;
import com.medshare.hub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmergencyAccessService {

    private final AuditService auditService;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public void performBreakGlass(Long userId, Long patientId, String reason) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        log.info("EMERGENCY BREAK-GLASS: User {} (ID: {}) accessed Patient ID: {} for reason: {}",
                user.getFullName(), userId, patientId, reason);

        // Create an enhanced audit log entry for emergency access
        auditService.logAccess(
                userId,
                patientId,
                "EMERGENCY_OVERRIDE",
                null, // Resource ID not applicable to general patient record access in this context
                AccessLog.Action.READ,
                AccessLog.Decision.PERMIT,
                "EmergencyOverridePolicy",
                null,
                true,
                reason,
                null, // IP
                null // Session
        );

        // Alert supervisors via notification service
        notificationService.sendEmergencyAlert(user, patientId, reason);
    }
}
