package org.telegrambots.doctortelegrambot.controllers;

import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.telegrambots.doctortelegrambot.entities.Patient;
import org.telegrambots.doctortelegrambot.entities.PatientState;
import org.telegrambots.doctortelegrambot.entities.Permission;
import org.telegrambots.doctortelegrambot.repositories.PermissionRepository;
import org.telegrambots.doctortelegrambot.services.AuthenticationService;
import org.telegrambots.doctortelegrambot.services.EmergencyService;
import org.telegrambots.doctortelegrambot.services.PatientService;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext
public class EmergencyRestControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AuthenticationService authenticationService;
    private RabbitTemplate rabbitTemplate;
    private EmergencyService emergencyService;

    @Mock
    private Patient patient;
    private final long CHAT_ID = 1231231;
    @Autowired
    private PatientService patientService;
    @Autowired
    private PermissionRepository permissionRepository;

    private static final PostgreSQLContainer postgresqlContainer;

    private static final RabbitMQContainer rabbitContainer;


    static {
        postgresqlContainer = new PostgreSQLContainer("postgres:latest")
                .withDatabaseName("telegramusers")
                .withUsername("telegrambot")
                .withPassword("telegrambot");
        postgresqlContainer.start();
    }

    static {

        rabbitContainer = new RabbitMQContainer("rabbitmq:3-management")
                .withExposedPorts(5672, 15672)
                .withStartupTimeout(Duration.ofSeconds(100));
        rabbitContainer.start();
    }

    @DynamicPropertySource
    public static void overrideProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", postgresqlContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", postgresqlContainer::getPassword);
        dynamicPropertyRegistry.add("spring.datasource.driver-class-name", postgresqlContainer::getDriverClassName);
    }

    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.rabbitmq.host", () -> rabbitContainer.getHost());
        dynamicPropertyRegistry.add("spring.rabbitmq.port", () -> rabbitContainer.getAmqpPort());
    }

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
