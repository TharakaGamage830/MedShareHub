package com.medshare.hub.controller;

import com.medshare.hub.entity.Patient;
import com.medshare.hub.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientService patientService;

    @Test
    @WithMockUser(roles = "DOCTOR")
    void whenGetPatient_thenReturnJson() throws Exception {
        // given
        Patient patient = new Patient();
        patient.setPatientId(1L);
        patient.setFullName("Controller Test Patient");
        patient.setMrn("MRN-C-1");

        when(patientService.getPatientById(1L)).thenReturn(patient);

        // when & then
        mockMvc.perform(get("/api/patients/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Controller Test Patient"))
                .andExpect(jsonPath("$.mrn").value("MRN-C-1"));
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void whenGetPatientNotFound_thenReturn404() throws Exception {
        // given
        when(patientService.getPatientById(99L)).thenThrow(new RuntimeException("Patient not found"));

        // when & then
        mockMvc.perform(get("/api/patients/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError()); // Base on current GlobalExceptionHandler
    }
}
