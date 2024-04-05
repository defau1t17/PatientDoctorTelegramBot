package org.patientbot.patienttelegrambot.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmergencyRequestServiceTest {

    @Mock
    private RestTemplate mockRestTemplate;

    private EmergencyRequestService emergencyRequestServiceUnderTest;

    @BeforeEach
    void setUp() {
        emergencyRequestServiceUnderTest = new EmergencyRequestService(mockRestTemplate);
    }

    @Test
    void testSendEmergencyHelpRequest() {
        when(mockRestTemplate.postForEntity(anyString(), any(), any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        final boolean result = emergencyRequestServiceUnderTest.sendEmergencyHelpRequest(0);
        assertTrue(result);
    }

    @Test
    void testSendEmergencyHelpRequest_RestTemplateThrowsRestClientException() {
        when(mockRestTemplate.postForEntity(anyString(), any(), any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        final boolean result = emergencyRequestServiceUnderTest.sendEmergencyHelpRequest(0);
        assertFalse(result);
    }
}
