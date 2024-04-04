package org.patientbot.patienttelegrambot.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
        // Setup
        // Configure RestTemplate.postForEntity(...).
        final ResponseEntity<ResponseEntity> responseEntityResponseEntity = new ResponseEntity<>(
                new ResponseEntity<>("body", HttpStatusCode.valueOf(0)), HttpStatusCode.valueOf(0));
        when(mockRestTemplate.postForEntity(eq("url"), any(Object.class), eq(ResponseEntity.class)))
                .thenReturn(responseEntityResponseEntity);

        // Run the test
        final boolean result = emergencyRequestServiceUnderTest.sendEmergencyHelpRequest(0L);

        // Verify the results
        assertFalse(result);
    }

    @Test
    void testSendEmergencyHelpRequest_RestTemplateThrowsRestClientException() {
        // Setup
        when(mockRestTemplate.postForEntity(eq("url"), any(Object.class), eq(ResponseEntity.class)))
                .thenThrow(RestClientException.class);

        // Run the test
        assertThrows(RestClientException.class, () -> emergencyRequestServiceUnderTest.sendEmergencyHelpRequest(0L));
    }
}
