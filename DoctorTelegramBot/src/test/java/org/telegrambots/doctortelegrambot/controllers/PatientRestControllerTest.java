package org.telegrambots.doctortelegrambot.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.telegrambots.doctortelegrambot.dto.PatientDTO;
import org.telegrambots.doctortelegrambot.entities.Patient;
import org.telegrambots.doctortelegrambot.entities.PatientState;
import org.telegrambots.doctortelegrambot.entities.Permission;
import org.telegrambots.doctortelegrambot.repositories.PermissionRepository;
import org.telegrambots.doctortelegrambot.services.PatientService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
public class PatientRestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Mock
    private Patient patient;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PatientService patientService;

    @Autowired
    private ObjectMapper objectMapper;
    private final long CHAT_ID = 13317711;

    @BeforeEach
    void init() {
        patient = new Patient(Permission.tokenFabric(permissionRepository), 0, "patient", "patient", "Huge brain", PatientState.STABLE, 133, "smart");
        patient = patientService.create(patient);
    }

    @Test
    void getPatientByID() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/v1/patient/" + patient.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void getPatientByIDNotFoundResponse() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/v1/patient/" + (-11)))
                .andExpect(status().isNotFound());
    }

    @Test
    void saveNewPatientTest() throws Exception {
        mockMvc.perform(post("http://localhost:8080/api/v1/patient")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(PatientDTO.covertPatientToDTO(patient))))
                .andExpect(status().isCreated());
    }

    @Test
    void deletePatientByIDTest() throws Exception {
        mockMvc.perform(delete("http://localhost:8080/api/v1/patient/" + patient.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void deletePatientByIDTestNotFound() throws Exception {
        mockMvc.perform(delete("http://localhost:8080/api/v1/patient/" + patient.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void findAllPatientsByPage() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/v1/patient")
                        .param("page", String.valueOf(0)).param("size", String.valueOf(1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*]").isNotEmpty());
    }


    @AfterEach
    void clear() {
        patientService.delete(patient);
        permissionRepository.delete(patient.getPersonalToken());
    }

}
