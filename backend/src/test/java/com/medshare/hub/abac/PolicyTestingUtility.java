package com.medshare.hub.abac;

import com.medshare.hub.abac.attributes.EnvironmentAttributes;
import com.medshare.hub.abac.attributes.ResourceAttributes;
import com.medshare.hub.abac.attributes.SubjectAttributes;
import com.medshare.hub.entity.MedicalRecord;
import com.medshare.hub.entity.User;
import com.medshare.hub.entity.Patient;

import java.time.LocalTime;
import java.util.Collections;

/**
 * PolicyTestingUtility - Reusable components for ABAC testing
 */
public class PolicyTestingUtility {

    public static SubjectAttributes createSubject(User user) {
        return SubjectAttributes.builder()
                .userId(user.getUserId())
                .role(user.getRole().name())
                .department(user.getDepartment())
                .certifications(user.getCertifications() != null ? user.getCertifications() : Collections.emptyList())
                .isEmergencyCertified(user.getIsEmergencyCertified() != null && user.getIsEmergencyCertified())
                .build();
    }

    public static ResourceAttributes createResource(MedicalRecord record) {
        return ResourceAttributes.builder()
                .recordId(record.getRecordId())
                .patientId(record.getPatient() != null ? record.getPatient().getPatientId() : null)
                .sensitivityLevel(record.getSensitivityLevel().name())
                .recordType(record.getRecordType().name())
                .build();
    }

    public static EnvironmentAttributes createEnvironment(String ip, LocalTime time) {
        return EnvironmentAttributes.builder()
                .currentTime(time)
                .ipAddress(ip)
                .deviceType("WEB")
                .isEmergency(false)
                .build();
    }

    public static EnvironmentAttributes createManualEmergencyEnv(String ip, String justification) {
        return EnvironmentAttributes.builder()
                .currentTime(LocalTime.now())
                .ipAddress(ip)
                .isEmergency(true)
                .justification(justification)
                .build();
    }
}
