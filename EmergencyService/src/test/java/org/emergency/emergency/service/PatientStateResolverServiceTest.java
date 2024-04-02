package org.emergency.emergency.service;

import org.emergency.emergency.dto.DoctorDTO;
import org.emergency.emergency.dto.PatientDTO;
import org.emergency.emergency.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PatientStateResolverServiceTest {

    @Mock
    private RabbitTemplate mockRabbitTemplate;

    private PatientStateResolverService patientStateResolverServiceUnderTest;

    @BeforeEach
    void setUp() {
        patientStateResolverServiceUnderTest = new PatientStateResolverService(mockRabbitTemplate);
    }

    @Test
    void testEmergencyCallerByPatientState() {
        // Setup
        final DoctorDTO doctorDTO = new DoctorDTO();
        doctorDTO.setId(0L);
        doctorDTO.setShiftStatus("shiftStatus");
        doctorDTO.setDoctorPosition("doctorPosition");
        final UserDTO user = new UserDTO();
        user.setChatID(0L);
        doctorDTO.setUser(user);
        final List<DoctorDTO> doctors = List.of(doctorDTO);
        final PatientDTO patient = new PatientDTO();
        patient.setId(0);
        patient.setChamberNumber(0);
        patient.setPatientState("patientState");

        // Run the test
        patientStateResolverServiceUnderTest.emergencyCallerByPatientState(doctors, patient);

        // Verify the results
        verify(mockRabbitTemplate).convertAndSend(eq("routingKey"), any(Object.class));
    }

    @Test
    void testEmergencyCallerByPatientState_RabbitTemplateThrowsAmqpException() {
        // Setup
        final DoctorDTO doctorDTO = new DoctorDTO();
        doctorDTO.setId(0L);
        doctorDTO.setShiftStatus("shiftStatus");
        doctorDTO.setDoctorPosition("doctorPosition");
        final UserDTO user = new UserDTO();
        user.setChatID(0L);
        doctorDTO.setUser(user);
        final List<DoctorDTO> doctors = List.of(doctorDTO);
        final PatientDTO patient = new PatientDTO();
        patient.setId(0);
        patient.setChamberNumber(0);
        patient.setPatientState("patientState");

        doThrow(AmqpException.class).when(mockRabbitTemplate).convertAndSend(eq("routingKey"), any(Object.class));

        // Run the test
        assertThrows(AmqpException.class,
                () -> patientStateResolverServiceUnderTest.emergencyCallerByPatientState(doctors, patient));
    }
}
