package com.medshare.hub.abac;

import com.medshare.hub.abac.attributes.EnvironmentAttributes;
import com.medshare.hub.abac.attributes.ResourceAttributes;
import com.medshare.hub.abac.attributes.SubjectAttributes;
import com.medshare.hub.abac.policies.EmergencyOverridePolicy;
import com.medshare.hub.abac.policies.PatientSelfAccessPolicy;
import com.medshare.hub.abac.policies.TreatingPhysicianPolicy;
import com.medshare.hub.repository.TreatmentRelationshipRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * PolicyEvaluatorTest - Unit tests for ABAC policy evaluation
 * 
 * Tests:
 * - Policy priority ordering
 * - Emergency override mechanism
 * - Patient self-access
 * - Treating physician access with business hours
 * - Deny by default
 * 
 * @author MedShare Development Team
 */
class PolicyEvaluatorTest {

    @Mock
    private TreatmentRelationshipRepository treatmentRelationshipRepository;

    private PolicyEvaluator policyEvaluator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create policies
        EmergencyOverridePolicy emergencyPolicy = new EmergencyOverridePolicy();
        PatientSelfAccessPolicy patientSelfAccessPolicy = new PatientSelfAccessPolicy();
        TreatingPhysicianPolicy treatingPhysicianPolicy = new TreatingPhysicianPolicy(treatmentRelationshipRepository);

        // Initialize evaluator with policies
        policyEvaluator = new PolicyEvaluator(Arrays.asList(
                emergencyPolicy,
                patientSelfAccessPolicy,
                treatingPhysicianPolicy));
    }

    @Test
    void testEmergencyOverride_WithValidCertificationAndJustification_ShouldPermit() {
        // Arrange
        SubjectAttributes subject = SubjectAttributes.builder()
                .userId(1L)
                .role("DOCTOR")
                .emergencyCertified(true)
                .build();

        ResourceAttributes resource = ResourceAttributes.builder()
                .resourceId(100L)
                .resourceType("MEDICAL_RECORD")
                .patientId(50L)
                .sensitivityLevel("CRITICAL")
                .build();

        EnvironmentAttributes environment = EnvironmentAttributes.builder()
                .currentTime(LocalDateTime.now())
                .isEmergency(true)
                .justification("Patient in critical condition, immediate access required for life-saving treatment")
                .build();

        // Act
        PolicyDecision decision = policyEvaluator.evaluateAccess(subject, resource, environment, "READ");

        // Assert
        assertTrue(decision.isPermitted(), "Emergency access should be permitted");
        assertEquals("EmergencyOverridePolicy", decision.getPolicyMatched());
        assertTrue(decision.hasObligation("enhanced_audit"));
        assertTrue(decision.hasObligation("supervisor_notification"));
    }

    @Test
    void testEmergencyOverride_WithoutJustification_ShouldDeny() {
        // Arrange
        SubjectAttributes subject = SubjectAttributes.builder()
                .userId(1L)
                .role("DOCTOR")
                .emergencyCertified(true)
                .build();

        ResourceAttributes resource = ResourceAttributes.builder()
                .resourceId(100L)
                .resourceType("MEDICAL_RECORD")
                .patientId(50L)
                .build();

        EnvironmentAttributes environment = EnvironmentAttributes.builder()
                .isEmergency(true)
                .justification("") // Empty justification
                .build();

        // Act
        PolicyDecision decision = policyEvaluator.evaluateAccess(subject, resource, environment, "READ");

        // Assert
        assertFalse(decision.isPermitted(), "Emergency access without justification should be denied");
    }

    @Test
    void testPatientSelfAccess_ShouldPermit() {
        // Arrange
        SubjectAttributes subject = SubjectAttributes.builder()
                .userId(50L)
                .role("PATIENT")
                .build();

        ResourceAttributes resource = ResourceAttributes.builder()
                .resourceId(100L)
                .resourceType("MEDICAL_RECORD")
                .patientId(50L)
                .sensitivityLevel("PSYCHIATRIC")
                .build();

        EnvironmentAttributes environment = EnvironmentAttributes.builder()
                .currentTime(LocalDateTime.now())
                .isEmergency(false)
                .build();

        // Act
        PolicyDecision decision = policyEvaluator.evaluateAccess(subject, resource, environment, "READ");

        // Assert
        assertTrue(decision.isPermitted(), "Patient should access their own records");
        assertEquals("PatientSelfAccessPolicy", decision.getPolicyMatched());
    }

    @Test
    void testTreatingPhysician_WithActiveRelationshipAndBusinessHours_ShouldPermit() {
        // Arrange
        SubjectAttributes subject = SubjectAttributes.builder()
                .userId(10L)
                .role("DOCTOR")
                .department("cardiology")
                .build();

        ResourceAttributes resource = ResourceAttributes.builder()
                .resourceId(100L)
                .resourceType("MEDICAL_RECORD")
                .patientId(50L)
                .sensitivityLevel("STANDARD")
                .build();

        EnvironmentAttributes environment = EnvironmentAttributes.builder()
                .currentTime(LocalDateTime.of(2026, 1, 30, 14, 0)) // 2 PM - business hours
                .isEmergency(false)
                .build();

        // Mock active relationship
        when(treatmentRelationshipRepository.hasActiveRelationship(10L, 50L))
                .thenReturn(true);

        // Act
        PolicyDecision decision = policyEvaluator.evaluateAccess(subject, resource, environment, "READ");

        // Assert
        assertTrue(decision.isPermitted(), "Treating physician should access patient records");
        assertEquals("TreatingPhysicianPolicy", decision.getPolicyMatched());
    }

    @Test
    void testTreatingPhysician_OutsideBusinessHours_ShouldDeny() {
        // Arrange
        SubjectAttributes subject = SubjectAttributes.builder()
            .userId(10L)
            .role("DOCTOR")
            .build();

        ResourceAttributes resource = ResourceAttributes.builder()
            .resourceId(100L)
            .resourceType("MEDICAL_RECORD")
            .patientId(50L)
            .sensitivityLevel("STANDARD")
            .build();

        EnvironmentAttributes environment = EnvironmentAttributes.builder()
            .currentTime(LocalDateTime.of(2026, 1, 30, 22, 0)) // 10 PM - outside business hours
            .isEmergency(false)
            .build();

        when(treatment RelationshipRepository.hasActiveRelationship(10L, 50L))
            .thenReturn(true);

        // Act
        PolicyDecision decision = policyEvaluator.evaluateAccess(subject, resource, environment, "READ");

        // Assert
        assertFalse(decision.isPermitted(), "Access outside business hours should be denied");
    }

    @Test
    void testNoApplicablePolicy_ShouldDenyByDefault() {
        // Arrange - Unknown role
        SubjectAttributes subject = SubjectAttributes.builder()
                .userId(99L)
                .role("UNKNOWN_ROLE")
                .build();

        ResourceAttributes resource = ResourceAttributes.builder()
                .resourceId(100L)
                .resourceType("MEDICAL_RECORD")
                .patientId(50L)
                .build();

        EnvironmentAttributes environment = EnvironmentAttributes.builder()
                .currentTime(LocalDateTime.now())
                .isEmergency(false)
                .build();

        // Act
        PolicyDecision decision = policyEvaluator.evaluateAccess(subject, resource, environment, "READ");

        // Assert
        assertFalse(decision.isPermitted(), "Unknown role should be denied by default");
        assertEquals("DefaultDeny", decision.getPolicyMatched());
    }
}
