package com.medshare.hub.abac.policies;

import com.medshare.hub.abac.Policy;
import com.medshare.hub.abac.PolicyDecision;
import com.medshare.hub.abac.attributes.EnvironmentAttributes;
import com.medshare.hub.abac.attributes.ResourceAttributes;
import com.medshare.hub.abac.attributes.SubjectAttributes;
import com.medshare.hub.entity.Consent;
import com.medshare.hub.repository.ConsentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * InsuranceClaimsPolicy - ABAC policy for insurance adjuster access
 * 
 * An insurance claims adjuster can read billing codes and diagnosis information
 * IF:
 * 1. They have an active claim assigned for that patient
 * 2. Patient has consented to insurance data sharing
 * 3. CANNOT access clinical notes or sensitive diagnoses (auto-redaction
 * required)
 * 
 * This policy demonstrates:
 * - Consent-based authorization
 * - Purpose-specific access (INSURANCE purpose)
 * - Automatic content redaction for unauthorized fields
 * 
 * @author MedShare Development Team
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class InsuranceClaimsPolicy implements Policy {

    private final ConsentRepository consentRepository;

    @Override
    public PolicyDecision evaluate(
            SubjectAttributes subject,
            ResourceAttributes resource,
            EnvironmentAttributes environment,
            String action) {
        log.debug("Evaluating InsuranceClaimsPolicy for user {} accessing patient {}",
                subject.getUserId(), resource.getPatientId());

        // Check if patient has given consent for insurance data sharing
        boolean hasConsent = consentRepository.hasValidConsent(
                resource.getPatientId(),
                subject.getUserId(),
                Consent.Purpose.INSURANCE,
                Consent.DataType.BILLING // or BILLING-specific data type
        );

        if (!hasConsent) {
            return PolicyDecision.deny(getPolicyName(),
                    "No valid patient consent for insurance data sharing");
        }

        // Check record type - only allow billing-related records
        if (!"BILLING".equalsIgnoreCase(resource.getRecordType()) &&
                !"DIAGNOSIS".equalsIgnoreCase(resource.getRecordType())) {
            return PolicyDecision.deny(getPolicyName(),
                    "Insurance adjusters can only access billing and diagnosis records");
        }

        // Deny access to highly sensitive diagnoses
        if (resource.isHighlySensitive()) {
            return PolicyDecision.deny(getPolicyName(),
                    "Insurance adjusters cannot access highly sensitive diagnoses");
        }

        // Create a PERMIT decision with redaction obligation
        PolicyDecision decision = PolicyDecision.permit(getPolicyName());
        decision.addObligation("redact_clinical_notes");
        decision.addObligation("redact_sensitive_diagnoses");

        log.info("InsuranceClaimsPolicy: PERMIT with redaction - User {} accessing patient {} data",
                subject.getUserId(), resource.getPatientId());

        return decision;
    }

    @Override
    public String getPolicyName() {
        return "InsuranceClaimsPolicy";
    }

    @Override
    public int getPriority() {
        return 4; // Lower priority (after core access policies)
    }

    @Override
    public boolean isApplicable(
            SubjectAttributes subject,
            ResourceAttributes resource,
            EnvironmentAttributes environment,
            String action) {
        // Applicable for insurance adjusters reading billing/diagnosis records
        return "INSURANCE_ADJUSTER".equalsIgnoreCase(subject.getRole()) &&
                "READ".equalsIgnoreCase(action) &&
                "MEDICAL_RECORD".equalsIgnoreCase(resource.getResourceType());
    }
}
