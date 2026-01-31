package com.medshare.hub.repository;

import com.medshare.hub.entity.MedicalRecord;
import com.medshare.hub.entity.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class MedicalRecordRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    private Patient patient;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setFirstName("Record");
        patient.setLastName("Patient");
        patient.setMrn("MRN-REC-1");
        entityManager.persist(patient);
    }

    @Test
    void whenFindByPatient_thenReturnRecords() {
        // given
        MedicalRecord record = new MedicalRecord();
        record.setPatient(patient);
        record.setRecordType(MedicalRecord.RecordType.DIAGNOSIS);
        record.setSensitivityLevel(MedicalRecord.SensitivityLevel.STANDARD);
        record.setContent(new HashMap<>());
        entityManager.persist(record);
        entityManager.flush();

        // when
        List<MedicalRecord> records = medicalRecordRepository
                .findByPatient_PatientId(patient.getPatientId(), org.springframework.data.domain.Pageable.unpaged())
                .getContent();

        // then
        assertFalse(records.isEmpty());
        assertEquals(1, records.size());
        assertEquals(patient.getPatientId(), records.get(0).getPatient().getPatientId());
    }
}
