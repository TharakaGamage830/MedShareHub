package com.medshare.hub.abac.policies;

import com.medshare.hub.abac.Policy;
import com.medshare.hub.abac.PolicyDecision;
import com.medshare.hub.abac.attributes.EnvironmentAttributes;
import com.medshare.hub.abac.attributes.ResourceAttributes;
import com.medshare.hub.abac.attributes.SubjectAttributes;
import com.medshare.hub.repository.TreatmentRelationshipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * TreatingPhysicianPolicy -ABAC policy for treating physician access
 * 
 * A physician can read medical records IF:
 * 1. They have an active treatment relationship with the patient
 * 2. Access occurs during business hours (8AM-8PM) OR it's an emergency
 * 3. Data sensitivity is not 'PSYCHIATRIC' unless physician is in psychiatry
 * department
 * 
 * This implements the principle that treating physicians should have access
 * to their patients' records, with appropriate restrictions for highly
 * sensitive data.
 * 
 * @author MedShare Development Team
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TreatingPhysicianPolicy implements Policy {

    private final TreatmentRelationshipRepository treatmentRelationshipRepository;

    @Override
    public PolicyDecision evaluate(
            SubjectAttributes subject,
            ResourceAttributes resource,
            EnvironmentAttributes environment,
            String action) {
        log.debug("Evaluating TreatingPhysicianPolicy for user {} accessing patient {}",
                subject.getUserId(), resource.getPatientId());

        // Check if user has active treatment relationship
        boolean hasActiveRelationship = treatmentRelationshipRepository.hasActiveRelationship(
                subject.getUserId(),
                resource.getPatientId());

        if (!hasActiveRelationship) {
            return PolicyDecision.deny(getPolicyName(),
                    "No active treatment relationship with patient");
        }

        // Check business hours unless it's an emergency
        if (!environment.isBusinessHours() && !environment.isEmergencyAccess()) {
            return PolicyDecision.deny(getPolicyName(),
                    "Access outside business hours requires emergency override");
        }

        // Check psychiatric data restriction
        if ("PSYCHIATRIC".equalsIgnoreCase(resource.getSensitivityLevel())) {
            if (!"psychiatry".equalsIgnoreCase(subject.getDepartment())) {
                return PolicyDecision.deny(getPolicyName(),
                        "Psychiatric records require psychiatry department affiliation");
            }
        }

        log.info("TreatingPhysicianPolicy: PERMIT - User {} has active treatment relationship with patient {}",
                subject.getUserId(), resource.getPatientId());

        return PolicyDecision.permit(getPolicyName());
    }

    @Override
    public String getPolicyName() {
        return "TreatingPhysicianPolicy";
    }

    @Override
    public int getPriority() {
        return 3; // Medium priority (after emergency and patient self-access)
    }

    @Override
    public boolean isApplicable(
            SubjectAttributes subject,
            ResourceAttributes resource,
            EnvironmentAttributes environment,
            String action) {
        // Applicable for doctors performing READ actions on medical records
        return "DOCTOR".equalsIgnoreCase(subject.getRole()) &&
                "READ".equalsIgnoreCase(action) &&
                "MEDICAL_RECORD".equalsIgnoreCase(resource.getResourceType());
    }
}
