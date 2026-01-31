package com.medshare.hub.service;

import com.medshare.hub.entity.Patient;
import com.medshare.hub.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * PatientService - Patient management operations
 * 
 * Handles:
 * - Patient registration
 * - Patient search and retrieval
 * - Demographics management
 * - MRN generation
 * 
 * @author MedShare Development Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PatientService {

    private final PatientRepository patientRepository;

    /**
     * Create new patient
     */
    @Transactional
    public Patient createPatient(Patient patient) {
        // Check if MRN already exists
        if (patient.getMrn() != null && patientRepository.existsByMrn(patient.getMrn())) {
            throw new IllegalArgumentException("MRN already exists: " + patient.getMrn());
        }

        // If no MRN provided, generate one
        if (patient.getMrn() == null) {
            patient.setMrn(generateMRN());
        }

        Patient savedPatient = patientRepository.save(patient);
        log.info("Created new patient: MRN {}", savedPatient.getMrn());

        return savedPatient;
    }

    /**
     * Get patient by ID (cached)
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "patients", key = "#patientId")
    public Patient getPatientById(Long patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + patientId));
    }

    /**
     * Get patient by MRN
     */
    @Transactional(readOnly = true)
    public Patient getPatientByMrn(String mrn) {
        return patientRepository.findByMrn(mrn)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with MRN: " + mrn));
    }

    /**
     * Search patients by name
     */
    @Transactional(readOnly = true)
    public List<Patient> searchPatients(String searchTerm) {
        return patientRepository.searchByName(searchTerm);
    }

    /**
     * Update patient demographics
     */
    @Transactional
    public Patient updatePatient(Patient patient) {
        if (!patientRepository.existsById(patient.getPatientId())) {
            throw new IllegalArgumentException("Patient not found: " + patient.getPatientId());
        }

        Patient updated = patientRepository.save(patient);
        log.info("Updated patient: {}", patient.getPatientId());

        return updated;
    }

    /**
     * Generate unique MRN (Medical Record Number)
     * Format: MRN-<timestamp>-<random>
     */
    private String generateMRN() {
        long timestamp = System.currentTimeMillis();
        int random = (int) (Math.random() * 10000);
        return String.format("MRN-%d-%04d", timestamp, random);
    }
}
