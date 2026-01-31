package com.medshare.hub.service;

import com.medshare.hub.entity.Consent;
import com.medshare.hub.entity.Patient;
import com.medshare.hub.repository.ConsentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ConsentManagementServiceTest {

    @Mock
    private ConsentRepository consentRepository;

    @InjectMocks
    private ConsentManagementService consentManagementService;

    private Consent consent;
    private Patient patient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        patient = new Patient();
        patient.setPatientId(1L);

        consent = new Consent();
        consent.setConsentId(10L);
        consent.setPatient(patient);
        consent.setDataType(Consent.DataType.LAB_RESULTS);
        consent.setPurpose(Consent.Purpose.TREATMENT);
        consent.setRevoked(false);
        consent.setExpiresAt(LocalDateTime.now().plusDays(30));
    }

    @Test
    void testCheckConsent_Valid_ShouldReturnTrue() {
        when(consentRepository.hasValidConsent(
                any(), any(), any(), any()))
                .thenReturn(true);

        boolean hasConsent = consentManagementService.hasValidConsent(1L, 1L, Consent.Purpose.TREATMENT,
                Consent.DataType.LAB_RESULTS);
        assertTrue(hasConsent);
    }

    @Test
    void testCheckConsent_None_ShouldReturnFalse() {
        when(consentRepository.hasValidConsent(any(), any(), any(), any()))
                .thenReturn(false);

        boolean hasConsent = consentManagementService.hasValidConsent(1L, 1L, Consent.Purpose.TREATMENT,
                Consent.DataType.LAB_RESULTS);
        assertFalse(hasConsent);
    }
}
