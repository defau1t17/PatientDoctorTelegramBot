package org.telegrambots.doctortelegrambot.controllers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.telegrambots.doctortelegrambot.entities.*;
import org.telegrambots.doctortelegrambot.repositories.PermissionRepository;
import org.telegrambots.doctortelegrambot.services.DoctorService;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@SpringBootTest
public class AuthenticationRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired

    private PermissionRepository permissionRepository;

    @Mock
    private Doctor doctor;

    @Autowired
    private DoctorService doctorService;

    @BeforeEach
    void preload() {
        doctor = new Doctor(Permission.tokenFabric(permissionRepository), 0, "test", "test", DoctorPosition.CARDIOLOGIST, 2133, DoctorShift.DAILY_SHIFT);
        doctorService.create(doctor);
    }

    @Test
    void testGetAuthenticationStatusUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/authenticate")
                        .param("chatID", "23131"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void clientAuthenticationByTokenTest() throws Exception {
        mockMvc.perform(post("/api/v1/authenticate")
                .param("chatID", "31231231")
                .param("token", doctor.getPersonalToken().getPermissionToken().toString())
        ).andExpect(status().isOk());
    }

    @Test
    void clientAuthenticationByTokenTestFailure() throws Exception {
        mockMvc.perform(post("/api/v1/authenticate")
                        .param("chatID", "312313231543543")
                        .param("token", UUID.randomUUID().toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAuthenticationStatusAuthorized() throws Exception {
        clientAuthenticationByTokenTest();
        mockMvc.perform(get("/api/v1/authenticate")
                        .param("chatID", "31231231"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteDisauthenticationSuccessTest() throws Exception {
        clientAuthenticationByTokenTest();
        mockMvc.perform(delete("/api/v1/authenticate")
                        .param("chatID", "31231231"))
                .andExpect(status().isOk());
    }
    @Test
    void deleteDisauthenticationFailureTest() throws Exception {
        clientAuthenticationByTokenTest();
        mockMvc.perform(delete("/api/v1/authenticate")
                        .param("chatID", "0000000"))
                .andExpect(status().isBadRequest());
    }


    @AfterEach
    void clear() {
        doctorService.delete(doctor);
        permissionRepository.delete(doctor.getPersonalToken());
    }

}
