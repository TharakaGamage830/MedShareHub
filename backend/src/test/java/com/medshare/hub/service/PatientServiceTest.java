package com.medshare.hub.service;

import com.medshare.hub.entity.Patient;
import com.medshare.hub.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    private Patient patient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        patient = new Patient();
        patient.setPatientId(1L);
        patient.setFullName("Jane Doe");
        patient.setMrn("MRN-J-1");
    }

    @Test
    void testGetPatientById_Found_ShouldReturnPatient() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        Patient result = patientService.getPatientById(1L);
        assertEquals("Jane Doe", result.getFullName());
    }

    @Test
    void testCreatePatient_Valid_ShouldSave() {
        when(patientRepository.save(any())).thenReturn(patient);
        Patient result = patientService.createPatient(patient);
        assertNotNull(result);
        verify(patientRepository).save(patient);
    }
}
