package com.medshare.hub.service;

import com.medshare.hub.abac.PolicyDecision;
import com.medshare.hub.abac.PolicyEvaluator;
import com.medshare.hub.abac.attributes.EnvironmentAttributes;
import com.medshare.hub.entity.MedicalRecord;
import com.medshare.hub.entity.Patient;
import com.medshare.hub.entity.User;
import com.medshare.hub.repository.MedicalRecordRepository;
import com.medshare.hub.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MedicalRecordServiceTest {

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PolicyEvaluator policyEvaluator;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private MedicalRecordService medicalRecordService;

    private User doctor;
    private Patient patient;
    private MedicalRecord record;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        doctor = new User();
        doctor.setUserId(1L);
        doctor.setRole(User.UserRole.DOCTOR);
        doctor.setDepartment("cardiology");

        patient = new Patient();
        patient.setPatientId(50L);

        record = new MedicalRecord();
        record.setRecordId(100L);
        record.setPatient(patient);
        record.setSensitivityLevel(MedicalRecord.SensitivityLevel.STANDARD);
        record.setRecordType(MedicalRecord.RecordType.DIAGNOSIS);

        Map<String, Object> content = new HashMap<>();
        content.put("clinicalNotes", "Patient has chest pain");
        content.put("diagnosis", "Angina");
        record.setContent(content);
    }

    @Test
    void testGetRecord_Permitted_ShouldReturnRecord() {
        // Arrange
        when(medicalRecordRepository.findById(100L)).thenReturn(Optional.of(record));
        when(userRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(policyEvaluator.evaluateAccess(any(), any(), any(), any()))
                .thenReturn(PolicyDecision.permit("TestPolicy"));

        // Act
        MedicalRecord result = medicalRecordService.getRecordWithAuthorization(
                100L, 1L, EnvironmentAttributes.builder().build());

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.getRecordId());
        verify(auditService).logAccess(eq(1L), eq(50L), anyString(), eq(100L), any(), any(), any(), any(), any(), any(),
                any(), any());
    }

    @Test
    void testGetRecord_Denied_ShouldThrowException() {
        // Arrange
        when(medicalRecordRepository.findById(100L)).thenReturn(Optional.of(record));
        when(userRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(policyEvaluator.evaluateAccess(any(), any(), any(), any()))
                .thenReturn(PolicyDecision.deny("TestPolicy", "Insufficient level"));

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> medicalRecordService.getRecordWithAuthorization(100L, 1L,
                EnvironmentAttributes.builder().build()));
    }

    @Test
    void testGetRecord_WithRedaction_ShouldRedactContent() {
        // Arrange
        when(medicalRecordRepository.findById(100L)).thenReturn(Optional.of(record));
        when(userRepository.findById(1L)).thenReturn(Optional.of(doctor));

        PolicyDecision decision = PolicyDecision.permit("InsurancePolicy");
        decision.addObligation("redact_clinical_notes");
        when(policyEvaluator.evaluateAccess(any(), any(), any(), any()))
                .thenReturn(decision);

        // Act
        MedicalRecord result = medicalRecordService.getRecordWithAuthorization(
                100L, 1L, EnvironmentAttributes.builder().build());

        // Assert
        assertNotNull(result);
        assertFalse(result.getContent().containsKey("clinicalNotes"));
        assertTrue(result.getContent().containsKey("diagnosis"));
    }
}
