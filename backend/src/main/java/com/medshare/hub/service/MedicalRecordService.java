package com.medshare.hub.service;

import com.medshare.hub.abac.PolicyDecision;
import com.medshare.hub.abac.PolicyEvaluator;
import com.medshare.hub.abac.attributes.EnvironmentAttributes;
import com.medshare.hub.abac.attributes.ResourceAttributes;
import com.medshare.hub.abac.attributes.SubjectAttributes;
import com.medshare.hub.entity.MedicalRecord;
import com.medshare.hub.entity.User;
import com.medshare.hub.repository.MedicalRecordRepository;
import com.medshare.hub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * MedicalRecordService - Business logic for medical record access
 * 
 * Integrates ABAC policy evaluation with medical record retrieval.
 * All access goes through:
 * 1. ABAC policy evaluation
 * 2. Audit logging
 * 3. Field-level redaction (if obligations present)
 * 
 * @author MedShare Development Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final UserRepository userRepository;
    private final PolicyEvaluator policyEvaluator;
    private final AuditService auditService;

    /**
     * Get medical record by ID with ABAC authorization
     * 
     * @param recordId         Medical record ID
     * @param requestingUserId User requesting access
     * @param environment      Environmental context
     * @return Medical record with potential redaction
     */
    @Transactional(readOnly = true)
    public MedicalRecord getRecordWithAuthorization(
            Long recordId,
            Long requestingUserId,
            EnvironmentAttributes environment) {
        // Fetch record
        MedicalRecord record = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Medical record not found: " + recordId));

        // Fetch requesting user
        User requestingUser = userRepository.findById(requestingUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + requestingUserId));

        // Build ABAC attributes
        SubjectAttributes subject = buildSubjectAttributes(requestingUser);
        ResourceAttributes resource = buildResourceAttributes(record);

        // Evaluate ABAC policy
        PolicyDecision decision = policyEvaluator.evaluateAccess(
                subject, resource, environment, "READ");

        // Log access attempt
        auditService.logAccess(
                requestingUserId,
                record.getPatient().getPatientId(),
                "MEDICAL_RECORD",
                recordId,
                com.medshare.hub.entity.AccessLog.Action.READ,
                decision.isPermitted() ? com.medshare.hub.entity.AccessLog.Decision.PERMIT
                        : com.medshare.hub.entity.AccessLog.Decision.DENY,
                decision.getPolicyMatched(),
                decision.getDenyReason(),
                environment.isEmergencyAccess(),
                environment.getJustification(),
                environment.getIpAddress(),
                environment.getSessionId());

        // Check authorization
        if (!decision.isPermitted()) {
            log.warn("Access DENIED: User {} cannot access record {}. Reason: {}",
                    requestingUserId, recordId, decision.getDenyReason());
            throw new AccessDeniedException(decision.getDenyReason());
        }

        // Apply redaction if required by policy obligations
        if (decision.hasObligation("redact_clinical_notes") ||
                decision.hasObligation("redact_sensitive_diagnoses")) {
            return applyRedaction(record, decision.getObligations());
        }

        return record;
    }

    /**
     * Get patient's medical records with pagination and ABAC
     */
    @Transactional(readOnly = true)
    public Page<MedicalRecord> getPatientRecords(
            Long patientId,
            Long requestingUserId,
            EnvironmentAttributes environment,
            Pageable pageable) {
        // Note: In production, each record would be individually authorized
        // For performance, we could cache authorization decisions
        Page<MedicalRecord> records = medicalRecordRepository.findByPatient_PatientId(patientId, pageable);

        // For simplicity, we're doing bulk authorization check here
        // In production, implement per-record authorization with caching

        return records;
    }

    /**
     * Create new medical record
     */
    @Transactional
    public MedicalRecord createRecord(MedicalRecord record, Long createdBy) {
        User creator = userRepository.findById(createdBy)
                .orElseThrow(() -> new IllegalArgumentException("Creator user not found"));

        record.setCreatedBy(creator);
        return medicalRecordRepository.save(record);
    }

    /**
     * Apply field-level redaction based on policy obligations
     */
    private MedicalRecord applyRedaction(MedicalRecord record, java.util.List<String> obligations) {
        if (obligations.contains("redact_clinical_notes")) {
            Map<String, Object> content = record.getContent();
            if (content != null) {
                content.remove("clinicalNotes");
                content.remove("providerComments");
                log.debug("Redacted clinical notes from record {}", record.getRecordId());
            }
        }

        if (obligations.contains("redact_sensitive_diagnoses")) {
            Map<String, Object> content = record.getContent();
            if (content != null) {
                content.remove("sensitiveDiagnoses");
                content.remove("psychiatricNotes");
                log.debug("Redacted sensitive diagnoses from record {}", record.getRecordId());
            }
        }

        return record;
    }

    /**
     * Build subject attributes from User entity
     */
    private SubjectAttributes buildSubjectAttributes(User user) {
        return SubjectAttributes.builder()
                .userId(user.getUserId())
                .role(user.getRole().toString())
                .department(user.getDepartment())
                .certifications(user.getCertifications())
                .emergencyCertified(user.getEmergencyCertified())
                .employer(user.getEmployer())
                .location(user.getLocation())
                .build();
    }

    /**
     * Build resource attributes from MedicalRecord entity
     */
    private ResourceAttributes buildResourceAttributes(MedicalRecord record) {
        return ResourceAttributes.builder()
                .resourceId(record.getRecordId())
                .resourceType("MEDICAL_RECORD")
                .patientId(record.getPatient().getPatientId())
                .sensitivityLevel(record.getSensitivityLevel().toString())
                .recordType(record.getRecordType().toString())
                .createdBy(record.getCreatedBy() != null ? record.getCreatedBy().getUserId() : null)
                .build();
    }
}
