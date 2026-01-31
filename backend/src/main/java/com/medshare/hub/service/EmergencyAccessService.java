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
                "PERMIT",
                "Break-glass triggered. Reason: " + reason);

        // TODO: In a real system, send email/SMS notification to supervisor
        notifySupervisor(user, patientId, reason);
    }

    private void notifySupervisor(User user, Long patientId, String reason) {
        log.warn("NOTIFICATION SENT: Supervisor alerted for emergency access by {}", user.getFullName());
    }
}
