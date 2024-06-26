package org.patientbot.patienttelegrambot.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.patientbot.patienttelegrambot.dtos.DoctorDTO;
import org.patientbot.patienttelegrambot.dtos.PagebleDoctorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoctorRequestServiceTest {

    @Mock
    private RestTemplate mockRestTemplate;

    private DoctorRequestService doctorRequestServiceUnderTest;

    @BeforeEach
    void setUp() {
        doctorRequestServiceUnderTest = new DoctorRequestService(mockRestTemplate);
    }

    @Test
    void testGetDoctors() {
        PagebleDoctorDTO pagebleDoctorDTO = new PagebleDoctorDTO();
        pagebleDoctorDTO.setContent(List.of(spy(DoctorDTO.class)));
        pagebleDoctorDTO.setTotalPages(1);
        when(mockRestTemplate.getForEntity(anyString(), any()))
                .thenReturn(new ResponseEntity<>(pagebleDoctorDTO, HttpStatus.OK));
        final Optional<PagebleDoctorDTO> result = doctorRequestServiceUnderTest.getDoctors(0, 1);
        assertTrue(result.isPresent());
        assertEquals(pagebleDoctorDTO, result.get());
    }

    @Test
    void testGetDoctors_RestTemplateThrowsRestClientException() {
        PagebleDoctorDTO pagebleDoctorDTO = new PagebleDoctorDTO();
        pagebleDoctorDTO.setContent(List.of());
        when(mockRestTemplate.getForEntity(anyString(), any()))
                .thenReturn(new ResponseEntity<>(pagebleDoctorDTO, HttpStatus.OK));
        final Optional<PagebleDoctorDTO> result = doctorRequestServiceUnderTest.getDoctors(-1, 1);
        assertTrue(result.isPresent());
        assertEquals(result.get().getContent().size(), 0);
    }

    @Test
    void testGetDoctorByID() {
        DoctorDTO spy = spy(DoctorDTO.class);
        when(mockRestTemplate.getForEntity(anyString(), any())).
                thenReturn(new ResponseEntity<>(spy, HttpStatus.OK));
        final Optional<DoctorDTO> result = doctorRequestServiceUnderTest.getDoctorByID(0L);
        assertTrue(result.isPresent());
        assertEquals(spy, result.get());
    }

    @Test
    void testGetDoctorByIDNotFound() {
        when(mockRestTemplate.getForEntity(anyString(), any())).
                thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        final Optional<DoctorDTO> result = doctorRequestServiceUnderTest.getDoctorByID(0L);
        assertTrue(result.isEmpty());
    }

}
