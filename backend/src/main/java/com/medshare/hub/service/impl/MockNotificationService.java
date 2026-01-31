package com.medshare.hub.service.impl;

import com.medshare.hub.entity.User;
import com.medshare.hub.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * MockNotificationService - Logs alerts to console/logs
 * In production, this would integrate with SMTP, Twilio, or AWS SNS.
 */
@Service
@Slf4j
public class MockNotificationService implements NotificationService {

    @Override
    public void sendEmergencyAlert(User user, Long patientId, String reason) {
        log.error(
                "🚨 EMERGENCY ALERT: Physician {} (ID: {}) performed break-glass access on Patient ID: {}. Reason: {}",
                user.getFullName(), user.getUserId(), patientId, reason);
        // Simulate high-priority alerting logic
    }

    @Override
    public void sendSecurityAlert(String message) {
        log.warn("⚠️ SECURITY ALERT: {}", message);
    }
}
