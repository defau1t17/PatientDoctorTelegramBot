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
import org.telegrambots.doctortelegrambot.dto.DoctorDTO;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShiftRequestServiceTest {

    @Mock
    private RestTemplate mockRestTemplate;

    private ShiftRequestService shiftRequestServiceUnderTest;

    private DoctorDTO doctorDTO;

    @BeforeEach
    void setUp() {
        doctorDTO = new DoctorDTO("test", 1, "test", "OPENED", 1, 0, spy(DoctorDTO.UserDTO.class));
        shiftRequestServiceUnderTest = new ShiftRequestService(mockRestTemplate);
    }

    @Test
    void testChangeDoctorShiftStatus() {
        when(mockRestTemplate.postForEntity(anyString(), any(), eq(DoctorDTO.class)))
                .thenReturn(new ResponseEntity<>(doctorDTO, HttpStatus.OK));
        final Optional<DoctorDTO> result = shiftRequestServiceUnderTest.changeDoctorShiftStatus(0L);
        assertTrue(result.isPresent());
        assertEquals(doctorDTO, result.get());
    }

    @Test
    void testChangeDoctorShiftStatus_RestTemplateThrowsRestClientException() {
        when(mockRestTemplate.postForEntity(anyString(), any(), eq(DoctorDTO.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        final Optional<DoctorDTO> result = shiftRequestServiceUnderTest.changeDoctorShiftStatus(0L);
        assertTrue(result.isEmpty());
    }
}
