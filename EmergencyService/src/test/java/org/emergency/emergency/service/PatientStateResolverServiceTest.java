package org.emergency.emergency.service;

import lombok.val;
import org.emergency.emergency.dto.DoctorDTO;
import org.emergency.emergency.dto.EmergencyDTO;
import org.emergency.emergency.dto.PatientDTO;
import org.emergency.emergency.dto.UserDTO;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.RabbitMQContainer;

import java.time.Duration;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientStateResolverServiceTest {

    @Mock
    private RabbitTemplate mockRabbitTemplate;

    @ClassRule
    public static GenericContainer rabbit = new GenericContainer("rabbitmq:3-management")
            .withExposedPorts(5672, 15672);

    private PatientStateResolverService patientStateResolverServiceUnderTest;

    private PatientDTO patientDTO;

    private DoctorDTO doctorDTO;


    @BeforeEach
    void setUp() {
        patientStateResolverServiceUnderTest = new PatientStateResolverService(mockRabbitTemplate);
        patientDTO = new PatientDTO(1, 1, "STABLE");
        doctorDTO = new DoctorDTO(1, "", "NURSE", new UserDTO(1));
    }

    public static class Initializer implements
            ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            val values = TestPropertyValues.of(
                    "spring.rabbitmq.host=" + rabbit.getContainerIpAddress(),
                    "spring.rabbitmq.port=" + rabbit.getMappedPort(5672)
            );
            values.applyTo(configurableApplicationContext);
        }
    }
//
//    static {
//        rabbitContainer = new RabbitMQContainer("rabbitmq:3.7.25-management-alpine")
//                .withExposedPorts(5672, 15672);
////                .withStartupTimeout(Duration.ofSeconds());
//        rabbitContainer.start();
//    }
//
//    @DynamicPropertySource
//    public static void properties(DynamicPropertyRegistry dynamicPropertyRegistry) {
//        dynamicPropertyRegistry.add("spring.rabbitmq.host", rabbitContainer::getHost);
//        dynamicPropertyRegistry.add("spring.rabbitmq.port", rabbitContainer::getAmqpPort);
//    }


    @Test
    void testEmergencyCallerWhenPatientStateIsStable() {
        patientStateResolverServiceUnderTest.emergencyCallerByPatientState(List.of(doctorDTO), patientDTO);
        verify(mockRabbitTemplate).convertAndSend("nurse", new EmergencyDTO(1, 1));
    }

    @Test
    void testEmergencyCallerWhenPatientStateIsHard() {
        patientDTO.setPatientState("HARD");
        DoctorDTO doc2 = new DoctorDTO(2, "", "SURGEON", new UserDTO(2));
        patientStateResolverServiceUnderTest.emergencyCallerByPatientState(List.of(doctorDTO, doc2), patientDTO);
        verify(mockRabbitTemplate).convertAndSend("nurse", new EmergencyDTO(1, 1));
        verify(mockRabbitTemplate).convertAndSend("doctor", new EmergencyDTO(1, 2));
    }

    @Test
    void testEmergencyCallerWhenPatientStateIsCritical() {
        patientDTO.setPatientState("CRITICAL");
        DoctorDTO doc2 = new DoctorDTO(2, "", "SURGEON", new UserDTO(2));
        DoctorDTO doc3 = new DoctorDTO(3, "", "PARAMEDIC", new UserDTO(3));
        patientStateResolverServiceUnderTest.emergencyCallerByPatientState(List.of(doctorDTO, doc2, doc3), patientDTO);
        verify(mockRabbitTemplate).convertAndSend("nurse", new EmergencyDTO(1, 1));
        verify(mockRabbitTemplate).convertAndSend("doctor", new EmergencyDTO(1, 2));
        verify(mockRabbitTemplate).convertAndSend("paramedic", new EmergencyDTO(1, 3));
    }


}