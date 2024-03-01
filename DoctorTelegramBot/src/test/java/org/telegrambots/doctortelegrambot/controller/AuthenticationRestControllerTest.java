package org.telegrambots.doctortelegrambot.controller;

import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.telegrambots.doctortelegrambot.contoller.AuthenticationRestController;
import org.telegrambots.doctortelegrambot.entity.*;
import org.telegrambots.doctortelegrambot.repository.PermissionRepository;
import org.telegrambots.doctortelegrambot.service.AuthenticationService;
import org.telegrambots.doctortelegrambot.service.DoctorService;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


//@SpringBootTest
@AutoConfigureMockMvc
//@WebMvcTest(AuthenticationRestController.class)
@SpringBootTest
public class AuthenticationRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired

    private PermissionRepository permissionRepository;
    @Autowired
    private AuthenticationService authenticationService;

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


    @AfterEach
    void clear() {
        doctorService.delete(doctor);
        permissionRepository.delete(doctor.getPersonalToken());
    }

}
