package org.telegrambots.doctortelegrambot.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.telegrambots.doctortelegrambot.entities.*;
import org.telegrambots.doctortelegrambot.repositories.PermissionRepository;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AuthenticationServiceTest {

    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private DoctorService doctorService;

    private Doctor doctor;

    private final long CHAT_ID = 111111;

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
    void preload() {
        doctor = new Doctor(Permission.tokenFabric(permissionRepository), 0, "test", "test", DoctorPosition.CARDIOLOGIST, 2133, DoctorShift.DAILY_SHIFT);
        doctorService.create(doctor);
    }

    @Test
    void validateBeforeAuthenticationSuccessTest() {
        assertTrue(authenticationService.validateBeforeAuthentication(doctor.getPersonalToken().getPermissionToken()));
    }

    @Test
    void validateBeforeAuthenticationFailureTest() {
        assertFalse(authenticationService.validateBeforeAuthentication(UUID.randomUUID()));
    }

    @Test
    void authenticateSuccessTest() {
        assertTrue(authenticationService.authenticate(CHAT_ID, doctor.getPersonalToken().getPermissionToken()));
    }

    @Test
    void authenticateFailureTest() {
        assertFalse(authenticationService.authenticate(CHAT_ID, UUID.randomUUID()));
    }


    @AfterEach
    void clear() {
        doctorService.delete(doctor);
        permissionRepository.delete(doctor.getPersonalToken());
    }

}
