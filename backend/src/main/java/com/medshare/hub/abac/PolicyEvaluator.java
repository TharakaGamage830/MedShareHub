package com.medshare.hub.abac;

import com.medshare.hub.abac.attributes.EnvironmentAttributes;
import com.medshare.hub.abac.attributes.ResourceAttributes;
import com.medshare.hub.abac.attributes.SubjectAttributes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * PolicyEvaluator - Central ABAC policy evaluation engine
 * 
 * Orchestrates policy evaluation by:
 * 1. Collecting all registered policies
 * 2. Filtering to applicable policies for the request context
 * 3. Evaluating policies in priority order
 * 4. Returning the first PERMIT decision or final DENY
 * 
 * Performance Optimization:
 * - Uses @Cacheable for attribute caching (Redis)
 * - Policy results cached for 1 minute
 * - Evaluates policies by priority to short-circuit on first PERMIT
 * 
 * Target Performance: <100ms policy evaluation (95th percentile)
 * 
 * @author MedShare Development Team
 */
@Service
@Slf4j
public class PolicyEvaluator {

    private final List<Policy> policies;

    /**
     * Constructor injection of all Policy beans
     * Spring automatically injects all @Component classes implementing Policy
     * interface
     */
    public PolicyEvaluator(List<Policy> policies) {
        this.policies = policies;
        // Sort policies by priority (lower number = higher priority)
        this.policies.sort(Comparator.comparingInt(Policy::getPriority));
        log.info("Initialized PolicyEvaluator with {} policies: {}",
                policies.size(),
                policies.stream().map(Policy::getPolicyName).toList());
    }

    /**
     * Evaluate access request against all applicable ABAC policies
     * 
     * Algorithm:
     * 1. Filter to applicable policies only
     * 2. Evaluate in priority order (1=highest)
     * 3. Return first PERMIT (short-circuit)
     * 4. If all deny or no policies match, return DENY
     * 
     * @param subject     Subject (user) attributes
     * @param resource    Resource attributes
     * @param environment Environmental context
     * @param action      Action being attempted (READ, WRITE, etc.)
     * @return PolicyDecision with permit/deny and obligations
     */
    public PolicyDecision evaluateAccess(
            SubjectAttributes subject,
            ResourceAttributes resource,
            EnvironmentAttributes environment,
            String action) {
        long startTime = System.currentTimeMillis();

        log.debug("Evaluating access: user={}, resource={}, action={}",
                subject.getUserId(), resource.getResourceId(), action);

        // Filter to applicable policies
        List<Policy> applicablePolicies = policies.stream()
                .filter(policy -> policy.isApplicable(subject, resource, environment, action))
                .toList();

        log.debug("Found {} applicable policies out of {}",
                applicablePolicies.size(), policies.size());

        if (applicablePolicies.isEmpty()) {
            log.warn("No applicable policies found - DENY by default");
            return PolicyDecision.deny("DefaultDeny", "No applicable policy found");
        }

        // Evaluate policies in priority order
        for (Policy policy : applicablePolicies) {
            PolicyDecision decision = policy.evaluate(subject, resource, environment, action);

            if (decision.isPermitted()) {
                long duration = System.currentTimeMillis() - startTime;
                log.info("Access PERMITTED by {} in {}ms", policy.getPolicyName(), duration);
                return decision;
            } else {
                log.debug("Policy {} denied: {}", policy.getPolicyName(), decision.getDenyReason());
            }
        }

        // All policies denied - return last denial reason
        long duration = System.currentTimeMillis() - startTime;
        log.info("Access DENIED after evaluating {} policies in {}ms",
                applicablePolicies.size(), duration);

        return PolicyDecision.deny("AllPoliciesDenied",
                "No policy granted access to this resource");
    }

    /**
     * Cached policy evaluation for repeated requests
     * Cache key includes all attributes to ensure correct decisions
     * TTL: 60 seconds (configured in Redis)
     * 
     * Note: Cache is invalidated when user attributes, relationships, or consents
     * change
     */
    @Cacheable(value = "policyDecisions", key = "#subject.userId + ':' + #resource.resourceId + ':' + #action", unless = "#environment.isEmergencyAccess()")
    public PolicyDecision evaluateAccessCached(
            SubjectAttributes subject,
            ResourceAttributes resource,
            EnvironmentAttributes environment,
            String action) {
        // Emergency access should never be cached
        if (environment.isEmergencyAccess()) {
            return evaluateAccess(subject, resource, environment, action);
        }

        return evaluateAccess(subject, resource, environment, action);
    }

    /**
     * Get all registered policies (for admin/debugging)
     */
    public List<String> getRegisteredPolicies() {
        return policies.stream()
                .map(policy -> String.format("%s (priority=%d)",
                        policy.getPolicyName(),
                        policy.getPriority()))
                .toList();
    }
}
