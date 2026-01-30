package com.medshare.hub.controller;

import com.medshare.hub.abac.attributes.EnvironmentAttributes;
import com.medshare.hub.entity.MedicalRecord;
import com.medshare.hub.service.MedicalRecordService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * MedicalRecordController - Medical record access endpoints
 * 
 * All endpoints pass through ABAC authorization.
 * Access is logged to audit trail.
 * 
 * Endpoints:
 * - GET /api/records/{id} - Get single record
 * - GET /api/records/patient/{patientId} - Get patient's records
 * - POST /api/records - Create new record
 * 
 * @author MedShare Development Team
 */
@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:3000" })
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    /**
     * Get medical record by ID
     * ABAC authorization performed in service layer
     */
    @GetMapping("/{recordId}")
    public ResponseEntity<MedicalRecord> getRecord(
            @PathVariable Long recordId,
            @RequestParam(required = false, defaultValue = "false") Boolean isEmergency,
            @RequestParam(required = false) String justification,
            Authentication authentication,
            HttpServletRequest request) {
        Long userId = extractUserIdFromAuth(authentication);

        // Build environment attributes
        EnvironmentAttributes environment = EnvironmentAttributes.builder()
                .currentTime(LocalDateTime.now())
                .ipAddress(request.getRemoteAddr())
                .isEmergency(isEmergency)
                .justification(justification)
                .sessionId(request.getSession().getId())
                .build();

        try {
            MedicalRecord record = medicalRecordService.getRecordWithAuthorization(
                    recordId, userId, environment);
            return ResponseEntity.ok(record);
        } catch (AccessDeniedException e) {
            log.warn("Access denied for user {} to record {}: {}", userId, recordId, e.getMessage());
            return ResponseEntity.status(403).build();
        }
    }

    /**
     * Get patient's medical records (paginated)
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<Page<MedicalRecord>> getPatientRecords(
            @PathVariable Long patientId,
            Pageable pageable,
            @RequestParam(required = false, defaultValue = "false") Boolean isEmergency,
            @RequestParam(required = false) String justification,
            Authentication authentication,
            HttpServletRequest request) {
        Long userId = extractUserIdFromAuth(authentication);

        EnvironmentAttributes environment = EnvironmentAttributes.builder()
                .currentTime(LocalDateTime.now())
                .ipAddress(request.getRemoteAddr())
                .isEmergency(isEmergency)
                .justification(justification)
                .sessionId(request.getSession().getId())
                .build();

        Page<MedicalRecord> records = medicalRecordService.getPatientRecords(
                patientId, userId, environment, pageable);

        return ResponseEntity.ok(records);
    }

    /**
     * Create new medical record
     */
    @PostMapping
    public ResponseEntity<MedicalRecord> createRecord(
            @RequestBody MedicalRecord record,
            Authentication authentication) {
        Long userId = extractUserIdFromAuth(authentication);

        MedicalRecord created = medicalRecordService.createRecord(record, userId);
        return ResponseEntity.status(201).body(created);
    }

    /**
     * Extract user ID from authentication
     * In production, this would come from JWT claims
     */
    private Long extractUserIdFromAuth(Authentication authentication) {
        // Placeholder: In production, extract from JWT token
        // For now, we'll assume userId is part of the authentication details
        return 1L; // TODO: Implement proper JWT user extraction
    }
}
