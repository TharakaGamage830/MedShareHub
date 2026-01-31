package com.medshare.hub.controller;

import com.medshare.hub.service.ComplianceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/compliance")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ComplianceController {

    private final ComplianceService complianceService;

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
