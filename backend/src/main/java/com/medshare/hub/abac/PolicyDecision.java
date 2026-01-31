package com.medshare.hub.abac;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * PolicyDecision - Result of ABAC policy evaluation
 * 
 * Represents the authorization decision made by the policy evaluator.
 * Includes the decision (PERMIT/DENY), the policy that matched, and any
 * obligations that must be fulfilled if access is granted.
 * 
 * Obligations are additional actions required when granting access,
 * such as enhanced audit logging or supervisor notifications for
 * emergency (break-glass) access.
 * 
 * @author MedShare Development Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PolicyDecision {

    /**
     * Whether access is permitted
     */
    private boolean permitted;

    /**
     * Name of the policy that made the decision
     */
    private String policyMatched;

    /**
     * Additional actions required when access is granted
     * Examples: "enhanced_audit", "supervisor_notification",
     * "require_justification"
     */
    private List<String> obligations;

    /**
     * Reason for denying access (if denied)
     */
    private String denyReason;

    /**
     * Factory method to create a PERMIT decision
     */
    public static PolicyDecision permit(String policyName) {
        return new PolicyDecision(true, policyName, new ArrayList<>(), null);
    }

    /**
     * Factory method to create a PERMIT decision with obligations
     */
    public static PolicyDecision permitWithObligations(String policyName, List<String> obligations) {
        return new PolicyDecision(true, policyName, obligations, null);
    }

    /**
     * Factory method to create a DENY decision
     */
    public static PolicyDecision deny(String policyName, String reason) {
        return new PolicyDecision(false, policyName, new ArrayList<>(), reason);
    }

    /**
     * Add an obligation to this decision
     */
    public void addObligation(String obligation) {
        if (this.obligations == null) {
            this.obligations = new ArrayList<>();
        }
        this.obligations.add(obligation);
    }

    /**
     * Check if decision has a specific obligation
     */
    public boolean hasObligation(String obligation) {
        return obligations != null && obligations.contains(obligation);
    }
}
