package com.medshare.hub.abac;

import com.medshare.hub.abac.attributes.EnvironmentAttributes;
import com.medshare.hub.abac.attributes.ResourceAttributes;
import com.medshare.hub.abac.attributes.SubjectAttributes;

/**
 * Policy - Interface for ABAC policies
 * 
 * Each policy implements this interface to evaluate authorization requests.
 * Policies check subject, resource, and environment attributes to determine
 * whether access should be granted.
 * 
 * Policy Priority:
 * 1. EmergencyOverridePolicy (highest - break-glass access)
 * 2. PatientSelfAccessPolicy (patients always access their own data)
 * 3. TreatingPhysicianPolicy (active treatment relationships)
 * 4. InsuranceClaimsPolicy (with consent checks)
 * 
 * @author MedShare Development Team
 */
public interface Policy {

    /**
     * Evaluate this policy for the given context
     * 
     * @param subject     Subject (user) requesting access
     * @param resource    Resource being accessed
     * @param environment Environmental context of the request
     * @param action      Action being performed (READ, WRITE, etc.)
     * @return PolicyDecision indicating permit/deny and any obligations
     */
    PolicyDecision evaluate(
            SubjectAttributes subject,
            ResourceAttributes resource,
            EnvironmentAttributes environment,
            String action);

    /**
     * Get the name of this policy
     */
    String getPolicyName();

    /**
     * Get the priority of this policy (lower number = higher priority)
     * Used to determine policy evaluation order
     */
    int getPriority();

    /**
     * Check if this policy is applicable to the given context
     * Returns true if this policy should be evaluated
     */
    boolean isApplicable(
            SubjectAttributes subject,
            ResourceAttributes resource,
            EnvironmentAttributes environment,
            String action);
}
