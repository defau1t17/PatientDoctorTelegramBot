package org.telegrambots.doctortelegrambot.controllers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.telegrambots.doctortelegrambot.entities.Patient;
import org.telegrambots.doctortelegrambot.entities.PatientState;
import org.telegrambots.doctortelegrambot.entities.Permission;
import org.telegrambots.doctortelegrambot.repositories.PermissionRepository;
import org.telegrambots.doctortelegrambot.services.AuthenticationService;
import org.telegrambots.doctortelegrambot.services.EmergencyService;
import org.telegrambots.doctortelegrambot.services.PatientService;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@AutoConfigureMockMvc
@SpringBootTest
public class EmergencyRestControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AuthenticationService authenticationService;
    private RabbitTemplate rabbitTemplate;
    private EmergencyService emergencyService;

    @Mock
    private Patient patient;
    private final int CHAT_ID = 1231231;
    @Autowired
    private PatientService patientService;
    @Autowired
    private PermissionRepository permissionRepository;

    @BeforeEach
    void create() {
        patient = new Patient(Permission.tokenFabric(permissionRepository), 0, "patient", "patient", "Huge brain", PatientState.STABLE, 1333, "smart");
        patientService.create(patient);
        authenticationService.authenticate(CHAT_ID, patient.getPersonalToken().getPermissionToken());
        rabbitTemplate = Mockito.mock(RabbitTemplate.class);
        emergencyService = new EmergencyService(rabbitTemplate);
    }

    @Test
    void requestEmergencyHelpSuccessTest() throws Exception {
        patient.getPersonalToken().setChatID(CHAT_ID);
        assertThatCode(() -> emergencyService.emergencyCallerByPatientState(patient))
                .doesNotThrowAnyException();
        mockMvc.perform(post("/api/v1/emergency")
                        .param("chatID", String.valueOf(CHAT_ID)))
                .andExpect(status().isOk());
        Mockito.verify(this.rabbitTemplate)
                .convertAndSend(eq("nurse"), eq(CHAT_ID));
    }

    @Test
    void requestEmergencyHelpFailureTest() throws Exception {
        mockMvc.perform(post("/api/v1/emergency")
                        .param("chatID", String.valueOf(-133)))
                .andExpect(status().isNotFound());
    }


    @AfterEach
    void clear() {
        patientService.delete(patient);
        permissionRepository.delete(patient.getPersonalToken());
    }
}
