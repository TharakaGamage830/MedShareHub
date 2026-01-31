package com.medshare.hub.controller;

import com.medshare.hub.service.ComplianceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;\r
import java.util.Map;

@RestController
@RequestMapping("/api/compliance")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ComplianceController {

    private final ComplianceService complianceService;

    @GetMapping("/anomalies")
    public ResponseEntity<List<Map<String, Object>>> getAnomalies() {
        // TODO: Implement actual anomaly detection logic
        return ResponseEntity.ok(java.util.Collections.emptyList());
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        // TODO: Implement actual statistics calculation
        return ResponseEntity.ok(Map.of(
                "activePatients", 0,
                "pendingConsents", 0,
                "emergencyAccess", 0));
    }

    @PostMapping("/reports/generate")
    public ResponseEntity<Map<String, Object>> generateReport(@RequestBody Map<String, Object> request) {
        String type = (String) request.get("type");
        Long patientId = request.containsKey("patientId") ? ((Number) request.get("patientId")).longValue() : null;

        return ResponseEntity.ok(complianceService.generateReport(type, patientId));
    }

    @GetMapping("/reports/download/{reportId}")
    public ResponseEntity<byte[]> downloadReport(@PathVariable String reportId) {
        byte[] data = complianceService.getReportData(reportId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"report_" + reportId + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }
}
