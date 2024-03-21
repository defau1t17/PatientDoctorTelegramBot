package org.telegrambots.doctortelegrambot.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.telegrambots.doctortelegrambot.entities.*;
import org.telegrambots.doctortelegrambot.repositories.PermissionRepository;
import org.telegrambots.doctortelegrambot.services.AuthenticationService;
import org.telegrambots.doctortelegrambot.services.DoctorService;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    private AuthenticationService authenticationService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private ObjectMapper objectMapper;
    private final long CHAT_ID = 13317711;

    private static final PostgreSQLContainer postgresqlContainer;


    static {
        postgresqlContainer = new PostgreSQLContainer("postgres:latest")
                .withDatabaseName("telegramusers")
                .withUsername("telegrambot")
                .withPassword("telegrambot");
        postgresqlContainer.start();
    }

    @DynamicPropertySource
    public static void overrideProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", postgresqlContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", postgresqlContainer::getPassword);
        dynamicPropertyRegistry.add("spring.datasource.driver-class-name", postgresqlContainer::getDriverClassName);
    }

    @BeforeEach
    void init() {
        doctor = new Doctor(Permission.tokenFabric(permissionRepository), 0, "test", "test", DoctorPosition.CARDIOLOGIST, 20, DoctorShift.DAILY_SHIFT);
        doctor = doctorService.create(doctor);
        authenticationService.authenticate(CHAT_ID, doctor.getPersonalToken().getPermissionToken());
        doctor.getPersonalToken().setChatID(CHAT_ID);
    }

    @Test
    void getAllDoctorsTest() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/v1/doctor"))
                .andExpect(status().isOk())
                .andExpect(result -> result.getResponse().getContentAsString());
    }

    @Test
    void getDoctorByID() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/v1/doctor/" + doctor.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void getDoctorByIDNotFoundResponse() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/v1/doctor/" + (-11)))
                .andExpect(status().isNotFound());
    }

    @Test
    void doctorShiftManipulationTest() throws Exception {
        mockMvc.perform(post("http://localhost:8080/api/v1/doctor/shift?chatID=" + CHAT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shiftStatus").value("OPENED"));
    }

    @Test
    void deleteDoctorByIDTest() throws Exception {
        mockMvc.perform(delete("http://localhost:8080/api/v1/doctor/" + doctor.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void saveNewDoctorTest() throws Exception {
        mockMvc.perform(post("http://localhost:8080/api/v1/doctor")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(doctor)))
                .andExpect(status().isCreated());
    }

    @AfterEach
    void clear() {
        doctorService.delete(doctor);
        permissionRepository.delete(doctor.getPersonalToken());
    }


}
