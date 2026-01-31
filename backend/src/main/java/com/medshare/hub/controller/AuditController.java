package com.medshare.hub.controller;

import com.medshare.hub.entity.AccessLog;
import com.medshare.hub.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AuditController - Endpoints for retrieving audit logs
 */
@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:3000" })
public class AuditController {

    private final AuditService auditService;

    /**
     * Get access logs for a specific patient
     * Used in Patient Portal and Provider Medical Record View
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<Page<AccessLog>> getPatientAccessLogs(
            @PathVariable Long patientId,
            Pageable pageable) {
        Page<AccessLog> logs = auditService.getPatientAccessLogs(patientId, pageable);
        return ResponseEntity.ok(logs);
    }

    /**
     * Get access logs for a specific user (Provider's activity)
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<AccessLog>> getUserAccessLogs(
            @PathVariable Long userId,
            Pageable pageable) {
        Page<AccessLog> logs = auditService.getUserAccessLogs(userId, pageable);
        return ResponseEntity.ok(logs);
    }
}
