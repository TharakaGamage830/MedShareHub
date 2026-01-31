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
import static org.mockito.Mockito.*;

class ConsentServiceTest {

    @Mock
    private ConsentRepository consentRepository;

    @InjectMocks
    private ConsentService consentService;

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
        consent.setConsentType(Consent.ConsentType.INSURANCE_SHARING);
        consent.setStatus(Consent.ConsentStatus.ACTIVE);
        consent.setExpirationDate(LocalDateTime.now().plusDays(30));
    }

    @Test
    void testCheckConsent_Active_ShouldReturnTrue() {
        when(consentRepository.findByPatientAndConsentTypeAndStatus(
                any(), any(), eq(Consent.ConsentStatus.ACTIVE)))
                .thenReturn(Optional.of(consent));

        boolean hasConsent = consentService.hasActiveConsent(patient, Consent.ConsentType.INSURANCE_SHARING);
        assertTrue(hasConsent);
    }

    @Test
    void testCheckConsent_None_ShouldReturnFalse() {
        when(consentRepository.findByPatientAndConsentTypeAndStatus(any(), any(), any()))
                .thenReturn(Optional.empty());

        boolean hasConsent = consentService.hasActiveConsent(patient, Consent.ConsentType.INSURANCE_SHARING);
        assertFalse(hasConsent);
    }
}
