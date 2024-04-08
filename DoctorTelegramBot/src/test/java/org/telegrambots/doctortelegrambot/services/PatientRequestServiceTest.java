package org.telegrambots.doctortelegrambot.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.telegrambots.doctortelegrambot.dto.NewPatientDTO;
import org.telegrambots.doctortelegrambot.dto.PaginatedPatientsDTO;
import org.telegrambots.doctortelegrambot.dto.PatientDTO;
import org.telegrambots.doctortelegrambot.entities.PatientState;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientRequestServiceTest {

    @Mock
    private RestTemplate mockRestTemplate;

    private PatientRequestService patientRequestServiceUnderTest;

    private PatientDTO patientDTO;

    @BeforeEach
    void setUp() {
        patientDTO = spy(PatientDTO.class);
        patientRequestServiceUnderTest = new PatientRequestService(mockRestTemplate);
    }

    @Test
    void testCreateNewPatient() {
        NewPatientDTO newPatientDTO = spy(NewPatientDTO.class);
        when(mockRestTemplate.postForEntity(anyString(), eq(newPatientDTO),
                eq(PatientDTO.class))).thenReturn(new ResponseEntity<>(patientDTO, HttpStatus.OK));
        final Optional<PatientDTO> result = patientRequestServiceUnderTest.createNewPatient(newPatientDTO);
        assertTrue(result.isPresent());
        assertEquals(patientDTO, result.get());
    }

    @Test
    void testCreateNewPatientFailure() {
        NewPatientDTO newPatientDTO = spy(NewPatientDTO.class);
        when(mockRestTemplate.postForEntity(anyString(), eq(newPatientDTO),
                eq(PatientDTO.class))).thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        final Optional<PatientDTO> result = patientRequestServiceUnderTest.createNewPatient(newPatientDTO);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPaginatedPatients() {
        PaginatedPatientsDTO paginatedPatientsDTO = new PaginatedPatientsDTO(List.of(patientDTO), 0);

        when(mockRestTemplate.getForEntity(anyString(), eq(PaginatedPatientsDTO.class)))
                .thenReturn(new ResponseEntity<>(paginatedPatientsDTO, HttpStatus.OK));

        final PaginatedPatientsDTO result = patientRequestServiceUnderTest.getPaginatedPatients(0, 0);
        assertEquals(paginatedPatientsDTO, result);
        assertEquals(result.getContent().size(), 1);
        assertEquals(result.getContent().get(0), patientDTO);
    }

    @Test
    void testGetPaginatedPatientsWithNoContent() {
        PaginatedPatientsDTO paginatedPatientsDTO = new PaginatedPatientsDTO(List.of(), 0);
        when(mockRestTemplate.getForEntity(anyString(), eq(PaginatedPatientsDTO.class)))
                .thenReturn(new ResponseEntity<>(paginatedPatientsDTO, HttpStatus.OK));
        final PaginatedPatientsDTO result = patientRequestServiceUnderTest.getPaginatedPatients(0, 0);
        assertEquals(paginatedPatientsDTO, result);
        assertEquals(result.getContent().size(), 0);

    }

    @Test
    void testGetPatientByID() {
        when(mockRestTemplate.getForEntity(anyString(), eq(PatientDTO.class))).thenReturn(new ResponseEntity<>(patientDTO, HttpStatus.OK));
        final Optional<PatientDTO> result = patientRequestServiceUnderTest.getPatientByID(0L);
        assertTrue(result.isPresent());
        assertEquals(patientDTO, result.get());
    }

    @Test
    void testGetPatientByIDNotFound() {
        when(mockRestTemplate.getForEntity(anyString(), eq(PatientDTO.class))).thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        final Optional<PatientDTO> result = patientRequestServiceUnderTest.getPatientByID(0L);
        assertTrue(result.isEmpty());
    }

    @Test
    void testCreatePatientToken() {

        when(mockRestTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(UUID.randomUUID().toString(), HttpStatus.OK));
        final String result = patientRequestServiceUnderTest.createPatientToken();
        assertFalse(result.isEmpty());
    }
}
