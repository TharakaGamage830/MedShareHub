package com.medshare.hub.service;

import com.medshare.hub.entity.Consent;
import com.medshare.hub.entity.Patient;
import com.medshare.hub.entity.User;
import com.medshare.hub.repository.ConsentRepository;
import com.medshare.hub.repository.PatientRepository;
import com.medshare.hub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ConsentManagementService - Patient consent management
 * 
 * Handles:
 * - Creating and revoking consents
 * - Consent validation
 * - Granular consent management
 * - Temporary access delegation
 * 
 * @author MedShare Development Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ConsentManagementService {

    private final ConsentRepository consentRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    /**
     * Grant consent to user or organization
     */
    @Transactional
    @CacheEvict(value = "policyDecisions", allEntries = true)
    public Consent grantConsent(
            Long patientId,
            Long grantedToUserId,
            String grantedToOrganization,
            Consent.DataType dataType,
            Consent.Purpose purpose,
            LocalDateTime expiresAt) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));

        Consent consent = new Consent();
        consent.setPatient(patient);

        if (grantedToUserId != null) {
            User grantedToUser = userRepository.findById(grantedToUserId)
                    .orElseThrow(() -> new IllegalArgumentException("Granted user not found"));
            consent.setGrantedToUser(grantedToUser);
        }

        consent.setGrantedToOrganization(grantedToOrganization);
        consent.setDataType(dataType);
        consent.setPurpose(purpose);
        consent.setExpiresAt(expiresAt);

        Consent savedConsent = consentRepository.save(consent);
        log.info("Consent granted: Patient {} to {} for {} purpose",
                patientId,
                grantedToUserId != null ? "user " + grantedToUserId : grantedToOrganization,
                purpose);

        return savedConsent;
    }

    /**
     * Revoke consent
     */
    @Transactional
    @CacheEvict(value = "policyDecisions", allEntries = true)
    public void revokeConsent(Long consentId, Long patientId) {
        Consent consent = consentRepository.findById(consentId)
                .orElseThrow(() -> new IllegalArgumentException("Consent not found"));

        // Verify patient owns this consent
        if (!consent.getPatient().getPatientId().equals(patientId)) {
            throw new IllegalArgumentException("Patient does not own this consent");
        }

        consent.revoke();
        consentRepository.save(consent);

        log.info("Consent revoked: Consent {} by patient {}", consentId, patientId);
    }

    /**
     * Get all active consents for a patient
     */
    @Transactional(readOnly = true)
    public List<Consent> getActiveConsents(Long patientId) {
        return consentRepository.findActiveConsentsByPatient(patientId);
    }

    /**
     * Get all consents (including revoked) for audit
     */
    @Transactional(readOnly = true)
    public List<Consent> getAllConsents(Long patientId) {
        return consentRepository.findByPatient_PatientIdOrderByGrantedAtDesc(patientId);
    }

    /**
     * Check if valid consent exists
     */
    @Transactional(readOnly = true)
    public boolean hasValidConsent(
            Long patientId,
            Long userId,
            Consent.Purpose purpose,
            Consent.DataType dataType) {
        return consentRepository.hasValidConsent(patientId, userId, purpose, dataType);
    }
}
