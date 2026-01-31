package com.medshare.hub.controller;

import com.medshare.hub.service.EmergencyAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/emergency")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EmergencyAccessController {

    private final EmergencyAccessService emergencyAccessService;

    @PostMapping("/break-glass")
    public ResponseEntity<Void> breakGlass(
            @RequestAttribute("userId") Long userId,
            @RequestBody Map<String, Object> payload) {

        Long patientId = ((Number) payload.get("patientId")).longValue();
        String reason = (String) payload.get("reason");

        emergencyAccessService.performBreakGlass(userId, patientId, reason);
        return ResponseEntity.ok().build();
    }
}
