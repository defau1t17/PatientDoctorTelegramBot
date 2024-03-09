package org.telegrambots.doctortelegrambot.controllers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.telegrambots.doctortelegrambot.entities.*;
import org.telegrambots.doctortelegrambot.repositories.PermissionRepository;
import org.telegrambots.doctortelegrambot.services.DoctorService;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class DoctorRestControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Mock
    private Doctor doctor;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private DoctorService doctorService;

    @BeforeEach
    void init() {
        doctor = new Doctor(Permission.tokenFabric(permissionRepository), 0, "test", "test", DoctorPosition.CARDIOLOGIST, 2133, DoctorShift.DAILY_SHIFT);
        doctor = doctorService.create(doctor);
    }

    @Test
    void getAllDoctorsTest() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/v1/doctor"))
                .andExpect(status().isOk())
                .andExpect(result -> result.getResponse().getContentAsString());
    }

//    @Test
//    void getDoctorByIDTest() throws Exception {
//        mockMvc.perform(get("http://localhost:8080/api/v1/doctor/" + doctor.getId()))
//                .andExpect(status().isOk())
//                .andExpect(result -> result.getResponse().getContentAsString());
//    }

//    @Test
//    void getDoctorByIDNotFoundTest() throws Exception {
//        mockMvc.perform(get("http://localhost:8080/api/v1/doctor/" + (-11)))
//                .andExpect(status().isNotFound())
//                .andExpect(result -> result.getResponse().getContentAsString());
//    }

    @AfterEach
    void clear() {
        doctorService.delete(doctor);
        permissionRepository.delete(doctor.getPersonalToken());
    }


}
