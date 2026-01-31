package com.medshare.hub.abac.policies;

import com.medshare.hub.abac.Policy;
import com.medshare.hub.abac.PolicyDecision;
import com.medshare.hub.abac.attributes.EnvironmentAttributes;
import com.medshare.hub.abac.attributes.ResourceAttributes;
import com.medshare.hub.abac.attributes.SubjectAttributes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * EmergencyOverridePolicy - ABAC policy for break-glass emergency access
 * 
 * An emergency-certified physician can access critical patient information
 * during declared emergencies REGARDLESS of other restrictions, BUT:
 * 1. User MUST be emergency-certified
 * 2. Request MUST be flagged as emergency
 * 3. MUST provide justification
 * 4. Triggers supervisor notification (obligation)
 * 5. Creates enhanced audit log entry (obligation)
 * 
 * This implements the "break-glass" mechanism for life-threatening situations
 * where immediate access to patient data is critical.
 * 
 * Priority: HIGHEST (evaluated first)
 * 
 * Ethical Consideration: Emergency access is monitored and audited more
 * strictly to prevent abuse while ensuring patient safety.
 * 
 * @author MedShare Development Team
 */
@Component
@Slf4j
public class EmergencyOverridePolicy implements Policy {

    @Override
    public PolicyDecision evaluate(
            SubjectAttributes subject,
            ResourceAttributes resource,
            EnvironmentAttributes environment,
            String action) {
        log.warn("Evaluating EmergencyOverridePolicy for user {} - EMERGENCY ACCESS",
                subject.getUserId());

        // Verify user is emergency-certified
        if (subject.getEmergencyCertified() == null || !subject.getEmergencyCertified()) {
            return PolicyDecision.deny(getPolicyName(),
                    "User is not emergency-certified for break-glass access");
        }

        // Verify this is flagged as an emergency request
        if (!environment.isEmergencyAccess()) {
            return PolicyDecision.deny(getPolicyName(),
                    "Request not flagged as emergency");
        }

        // Require justification for emergency access
        if (environment.getJustification() == null || environment.getJustification().trim().isEmpty()) {
            return PolicyDecision.deny(getPolicyName(),
                    "Emergency access requires justification");
        }

        // Justify justification has meaningful content (at least 10 characters)
        if (environment.getJustification().trim().length() < 10) {
            return PolicyDecision.deny(getPolicyName(),
                    "Emergency justification must be detailed (minimum 10 characters)");
        }

        // Grant access with strict obligations
        PolicyDecision decision = PolicyDecision.permitWithObligations(
                getPolicyName(),
                Arrays.asList(
                        "enhanced_audit", // Create detailed audit log
                        "supervisor_notification", // Notify supervisor immediately
                        "require_justification", // Record justification
                        "temporary_access" // Access is time-limited
                ));

        log.warn(
                "EmergencyOverridePolicy: PERMIT - EMERGENCY ACCESS granted to user {} for patient {}. Justification: {}",
                subject.getUserId(), resource.getPatientId(), environment.getJustification());

        return decision;
    }

    @Override
    public String getPolicyName() {
        return "EmergencyOverridePolicy";
    }

    @Override
    public int getPriority() {
        return 1; // Highest priority - evaluated first
    }

    @Override
    public boolean isApplicable(
            SubjectAttributes subject,
            ResourceAttributes resource,
            EnvironmentAttributes environment,
            String action) {
        // Only applicable when emergency flag is set
        return environment.isEmergencyAccess();
    }
}
