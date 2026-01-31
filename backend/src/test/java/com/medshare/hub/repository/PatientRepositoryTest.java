package com.medshare.hub.repository;

import com.medshare.hub.entity.Patient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class PatientRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PatientRepository patientRepository;

    @Test
    void whenFindByMrn_thenReturnPatient() {
        // given
        Patient patient = new Patient();
        patient.setFirstName("Test");
        patient.setLastName("Patient");
        patient.setMrn("MRN-TEST-123");
        entityManager.persist(patient);
        entityManager.flush();

        // when
        Optional<Patient> found = patientRepository.findByMrn("MRN-TEST-123");

        // then
        assertTrue(found.isPresent());
        assertEquals(patient.getMrn(), found.get().getMrn());
    }

    @Test
    void whenFindByIdNotExists_thenReturnEmpty() {
        // when
        Optional<Patient> found = patientRepository.findById(999L);

        // then
        assertFalse(found.isPresent());
    }
}
