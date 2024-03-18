package org.telegrambots.doctortelegrambot.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.telegrambots.doctortelegrambot.entities.Patient;
import org.telegrambots.doctortelegrambot.entities.PatientState;
import org.telegrambots.doctortelegrambot.entities.Permission;
import org.telegrambots.doctortelegrambot.repositories.PermissionRepository;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
class EmergencyServiceTest {
    private RabbitTemplate rabbitTemplate;
    private EmergencyService emergencyService;
    @Autowired
    private PermissionRepository permissionRepository;
    private final long CHAT_ID = 1231231;
    private Patient patient;


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
        rabbitTemplate = Mockito.mock(RabbitTemplate.class);
        emergencyService = new EmergencyService(rabbitTemplate);
        patient = new Patient(Permission.tokenFabric(permissionRepository), 0, "patient", "patient", "Huge brain", PatientState.STABLE, 1333, "smart");
        patient.getPersonalToken().setChatID(CHAT_ID);
    }

    @Test
    void emergencyCallerForNurseTest() {
        assertThatCode(() -> emergencyService.emergencyCallerByPatientState(patient))
                .doesNotThrowAnyException();
        Mockito.verify(this.rabbitTemplate)
                .convertAndSend(eq("nurse"), eq(CHAT_ID));
    }

    @Test
    void emergencyCallerForDoctorAndNurseTest() {
        patient.setPatientState(PatientState.HARD);
        assertThatCode(() -> emergencyService.emergencyCallerByPatientState(patient))
                .doesNotThrowAnyException();
        Mockito.verify(this.rabbitTemplate)
                .convertAndSend(eq("nurse"), eq(CHAT_ID));
        Mockito.verify(this.rabbitTemplate)
                .convertAndSend(eq("doctor"), eq(CHAT_ID));
    }

    @Test
    void emergencyCallerForDoctorNurseAndParamedicTest() {
        patient.setPatientState(PatientState.CRITICAL);
        assertThatCode(() -> emergencyService.emergencyCallerByPatientState(patient))
                .doesNotThrowAnyException();
        Mockito.verify(this.rabbitTemplate)
                .convertAndSend(eq("nurse"), eq(CHAT_ID));
        Mockito.verify(this.rabbitTemplate)
                .convertAndSend(eq("doctor"), eq(CHAT_ID));
        Mockito.verify(this.rabbitTemplate)
                .convertAndSend(eq("paramedic"), eq(CHAT_ID));
    }

    @AfterEach
    void clear() {
        permissionRepository.delete(patient.getPersonalToken());
    }


}