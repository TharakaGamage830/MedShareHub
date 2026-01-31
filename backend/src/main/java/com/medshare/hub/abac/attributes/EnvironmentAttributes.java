package com.medshare.hub.abac.attributes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * EnvironmentAttributes - Contextual attributes of the access request
 * 
 * These attributes describe the environment and context in which
 * the access request is being made.
 * 
 * Key attributes:
 * - currentTime: When the access is requested
 * - ipAddress: IP address of the requester
 * - deviceType: Type of device (DESKTOP, MOBILE, TABLET)
 * - isEmergency: Whether this is an emergency (break-glass) access
 * - justification: Reason for access (required for emergency)
 * 
 * @author MedShare Development Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnvironmentAttributes {

    private LocalDateTime currentTime;
    private String ipAddress;
    private String deviceType;
    private Boolean isEmergency;
    private String justification;
    private String sessionId;

    /**
     * Check if current time is within business hours (8AM - 8PM)
     * Used by TreatingPhysicianPolicy
     */
    public boolean isBusinessHours() {
        if (currentTime == null) {
            currentTime = LocalDateTime.now();
        }
        LocalTime time = currentTime.toLocalTime();
        return time.isAfter(LocalTime.of(8, 0)) && time.isBefore(LocalTime.of(20, 0));
    }

    /**
     * Check if this is a weekend
     */
    public boolean isWeekend() {
        if (currentTime == null) {
            currentTime = LocalDateTime.now();
        }
        int dayOfWeek = currentTime.getDayOfWeek().getValue();
        return dayOfWeek == 6 || dayOfWeek == 7; // Saturday or Sunday
    }

    /**
     * Check if this is an emergency access request
     */
    public boolean isEmergencyAccess() {
        return isEmergency != null && isEmergency;
    }
}
