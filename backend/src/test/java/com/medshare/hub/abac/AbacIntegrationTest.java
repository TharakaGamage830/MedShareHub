package com.medshare.hub.abac;

import com.medshare.hub.abac.attributes.EnvironmentAttributes;
import com.medshare.hub.entity.User;
import com.medshare.hub.entity.Patient;
import com.medshare.hub.entity.MedicalRecord;
import com.medshare.hub.repository.UserRepository;
import com.medshare.hub.repository.PatientRepository;
import com.medshare.hub.repository.MedicalRecordRepository;
import com.medshare.hub.service.MedicalRecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AbacIntegrationTest {

    @Autowired
    private MedicalRecordService medicalRecordService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    private User doctor;
    private Patient patient;
    private MedicalRecord record;

    @BeforeEach
    void setUp() {
        // Create a test doctor
        doctor = new User();
        doctor.setEmail("dr.test@medshare.com");
        doctor.setFirstName("Dr.");
        doctor.setLastName("Test");
        doctor.setRole(User.UserRole.DOCTOR);
        doctor.setDepartment("cardiology");
        doctor.setPasswordHash("hashed");
        userRepository.save(doctor);

        // Create a test patient
        patient = new Patient();
        patient.setFirstName("John");
        patient.setLastName("Patient");
        patient.setMrn("MRN123");
        patientRepository.save(patient);

        // Create a test record
        record = new MedicalRecord();
        record.setPatient(patient);
        record.setRecordType(MedicalRecord.RecordType.DIAGNOSIS);
        record.setSensitivityLevel(MedicalRecord.SensitivityLevel.STANDARD);
        record.setContent(new HashMap<>());
        record.getContent().put("diagnosis", "Normal");
        medicalRecordRepository.save(record);
    }

    @Test
    void testDoctorAccess_NoRelationship_ShouldDeny() {
        EnvironmentAttributes env = EnvironmentAttributes.builder().build();

        assertThrows(AccessDeniedException.class,
                () -> medicalRecordService.getRecordWithAuthorization(record.getRecordId(), doctor.getUserId(), env));
    }

    @Test
    void testPatientAccess_OwnRecord_ShouldPermit() {
        // Create user for patient
        User patientUser = new User();
        patientUser.setEmail("patient@test.com");
        patientUser.setRole(User.UserRole.PATIENT);
        patient.setUser(patientUser);
        patientRepository.save(patient);
        userRepository.save(patientUser);

        EnvironmentAttributes env = EnvironmentAttributes.builder().build();

        MedicalRecord accessedRecord = medicalRecordService.getRecordWithAuthorization(
                record.getRecordId(), patientUser.getUserId(), env);

        assertNotNull(accessedRecord);
        assertEquals(record.getRecordId(), accessedRecord.getRecordId());
    }
}
