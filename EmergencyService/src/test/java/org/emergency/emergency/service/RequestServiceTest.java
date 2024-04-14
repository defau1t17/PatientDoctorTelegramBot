package org.emergency.emergency.service;

import org.emergency.emergency.dto.DoctorDTO;
import org.emergency.emergency.dto.PatientDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


import javax.print.Doc;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private RequestService requestServiceUnderTest;

    @BeforeEach
    void setUp() {
        requestServiceUnderTest = new RequestService(restTemplate);
    }

    @Test
    public void testGetPatientByChatID_Success() {
        PatientDTO patientDTO = spy(PatientDTO.class);
        when(restTemplate.getForEntity(anyString(), any()))
                .thenReturn(new ResponseEntity<>(patientDTO, HttpStatus.OK));

        Optional<PatientDTO> optionalPatient = requestServiceUnderTest.getPatientByChatID(0);

        assertTrue(optionalPatient.isPresent());
        assertEquals(patientDTO, optionalPatient.get());

        verify(restTemplate).getForEntity(anyString(), eq(PatientDTO.class));
    }

    @Test
    public void testGetPatientByChatIDFailure() {
        when(restTemplate.getForEntity(anyString(), any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        Optional<PatientDTO> optionalPatient = requestServiceUnderTest.getPatientByChatID(-1);

        assertTrue(optionalPatient.isEmpty());

        verify(restTemplate).getForEntity(anyString(), eq(PatientDTO.class));
    }


    @Test
    public void testGetDoctorsByPatientID() {
        when(restTemplate.getForEntity(anyString(), any()))
                .thenReturn(new ResponseEntity<>(new DoctorDTO[]{spy(DoctorDTO.class), spy(DoctorDTO.class)}, HttpStatus.OK));

        Optional<List<DoctorDTO>> doctorsByPatientID = requestServiceUnderTest.getDoctorsByPatientID(0);

        assertTrue(doctorsByPatientID.isPresent());
        assertEquals(doctorsByPatientID.get().size(), 2);

        verify(restTemplate).getForEntity(anyString(), eq(DoctorDTO[].class));
    }

    @Test
    public void testGetDoctorsByPatientIDFailure() {
        when(restTemplate.getForEntity(anyString(), any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        Optional<List<DoctorDTO>> doctorsByPatientID = requestServiceUnderTest.getDoctorsByPatientID(0);

        assertTrue(doctorsByPatientID.isEmpty());

        verify(restTemplate).getForEntity(anyString(), eq(DoctorDTO[].class));
    }


}
