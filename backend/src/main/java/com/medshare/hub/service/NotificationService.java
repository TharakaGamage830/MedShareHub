package com.medshare.hub.service;

import com.medshare.hub.entity.User;

/**
 * NotificationService - Handles multi-channel alerts
 */
public interface NotificationService {
    void sendEmergencyAlert(User user, Long patientId, String reason);

    void sendSecurityAlert(String message);
}
