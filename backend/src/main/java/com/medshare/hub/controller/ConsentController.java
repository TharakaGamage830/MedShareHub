package com.medshare.hub.controller;

import com.medshare.hub.entity.Consent;
import com.medshare.hub.service.ConsentManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * ConsentController - Patient consent management endpoints
 * 
 * Endpoints:
 * - GET /api/consents/patient/{patientId} - Get patient's consents
 * - POST /api/consents - Grant consent
 * - DELETE /api/consents/{consentId} - Revoke consent
 * 
 * @author MedShare Development Team
 */
@RestController
@RequestMapping("/api/consents")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:3000" })
public class ConsentController {

    private final ConsentManagementService consentManagementService;

    /**
     * Get active consents for a patient
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Consent>> getPatientConsents(@PathVariable Long patientId) {
        List<Consent> consents = consentManagementService.getActiveConsents(patientId);
        return ResponseEntity.ok(consents);
    }

    /**
     * Get all consents (including revoked) for audit
     */
    @GetMapping("/patient/{patientId}/all")
    public ResponseEntity<List<Consent>> getAllPatientConsents(@PathVariable Long patientId) {
        List<Consent> consents = consentManagementService.getAllConsents(patientId);
        return ResponseEntity.ok(consents);
    }

    /**
     * Grant consent
     */
    @PostMapping
    public ResponseEntity<Consent> grantConsent(@RequestBody Map<String, Object> request) {
        Long patientId = Long.parseLong(request.get("patientId").toString());
        Long grantedToUserId = request.get("grantedToUserId") != null
                ? Long.parseLong(request.get("grantedToUserId").toString())
                : null;
        String grantedToOrganization = (String) request.get("grantedToOrganization");
        Consent.DataType dataType = Consent.DataType.valueOf((String) request.get("dataType"));
        Consent.Purpose purpose = Consent.Purpose.valueOf((String) request.get("purpose"));

        LocalDateTime expiresAt = request.get("expiresAt") != null
                ? LocalDateTime.parse((String) request.get("expiresAt"))
                : null;

        Consent consent = consentManagementService.grantConsent(
                patientId, grantedToUserId, grantedToOrganization,
                dataType, purpose, expiresAt);

        return ResponseEntity.status(201).body(consent);
    }

    /**
     * Revoke consent
     */
    @DeleteMapping("/{consentId}")
    public ResponseEntity<Map<String, String>> revokeConsent(
            @PathVariable Long consentId,
            @RequestParam Long patientId) {
        consentManagementService.revokeConsent(consentId, patientId);
        return ResponseEntity.ok(Map.of("message", "Consent revoked successfully"));
    }
}
