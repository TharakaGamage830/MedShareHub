package com.medshare.hub.controller;

import com.medshare.hub.entity.Patient;
import com.medshare.hub.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * PatientController - Patient management endpoints
 * 
 * Endpoints:
 * - GET /api/patients/{id} - Get patient by ID
 * - GET /api/patients/mrn/{mrn} - Get patient by MRN
 * - GET /api/patients/search - Search patients
 * - POST /api/patients - Create patient
 * - PUT /api/patients/{id} - Update patient
 * 
 * @author MedShare Development Team
 */
@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:3000" })
public class PatientController {

    private final PatientService patientService;

    /**
     * Get patient by ID
     */
    @GetMapping("/{patientId}")
    public ResponseEntity<Patient> getPatient(@PathVariable Long patientId) {
        Patient patient = patientService.getPatientById(patientId);
        return ResponseEntity.ok(patient);
    }

    /**
     * Get patient by MRN
     */
    @GetMapping("/mrn/{mrn}")
    public ResponseEntity<Patient> getPatientByMrn(@PathVariable String mrn) {
        Patient patient = patientService.getPatientByMrn(mrn);
        return ResponseEntity.ok(patient);
    }

    /**
     * Search patients by name
     */
    @GetMapping("/search")
    public ResponseEntity<List<Patient>> searchPatients(@RequestParam String query) {
        List<Patient> patients = patientService.searchPatients(query);
        return ResponseEntity.ok(patients);
    }

    /**
     * Create new patient
     */
    @PostMapping
    public ResponseEntity<Patient> createPatient(@Valid @RequestBody Patient patient) {
        Patient created = patientService.createPatient(patient);
        return ResponseEntity.status(201).body(created);
    }

    /**
     * Update patient
     */
    @PutMapping("/{patientId}")
    public ResponseEntity<Patient> updatePatient(
            @PathVariable Long patientId,
            @Valid @RequestBody Patient patient) {
        patient.setPatientId(patientId);
        Patient updated = patientService.updatePatient(patient);
        return ResponseEntity.ok(updated);
    }
}
