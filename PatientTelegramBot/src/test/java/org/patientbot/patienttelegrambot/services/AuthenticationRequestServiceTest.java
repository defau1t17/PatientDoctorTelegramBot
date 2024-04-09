package org.patientbot.patienttelegrambot.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.patientbot.patienttelegrambot.dtos.AuthenticateDTO;
import org.patientbot.patienttelegrambot.dtos.AuthenticatedUserDTO;
import org.patientbot.patienttelegrambot.dtos.ChatStateDTO;
import org.patientbot.patienttelegrambot.entity.ChatStates;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationRequestServiceTest {

    @Mock
    private RestTemplate mockRestTemplate;

    private AuthenticationRequestService authenticationRequestServiceUnderTest;

    @BeforeEach
    void setUp() {
        authenticationRequestServiceUnderTest = new AuthenticationRequestService(mockRestTemplate);
    }


    @Test
    void testAuthenticate() {
        AuthenticateDTO spy = spy(AuthenticateDTO.class);
        when(mockRestTemplate.exchange(anyString(), eq(HttpMethod.PATCH), any(), eq(AuthenticateDTO.class)))
                .thenReturn(new ResponseEntity<>(spy, HttpStatus.OK));
        final Optional<AuthenticateDTO> result = authenticationRequestServiceUnderTest.authenticate(0L, "token");
        assertTrue(result.isPresent());
        assertEquals(spy, result.get());
    }

    @Test
    void testAuthenticate_RestTemplateThrowsRestClientException() {
        when(mockRestTemplate.exchange(anyString(), eq(HttpMethod.PATCH), any(), eq(AuthenticateDTO.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        final Optional<AuthenticateDTO> result = authenticationRequestServiceUnderTest.authenticate(0L, "token");
        assertTrue(result.isEmpty());
    }

    @Test
    void getAuthenticationStatusSuccess() {
        AuthenticateDTO authenticateDTO = spy(AuthenticateDTO.class);
        when(mockRestTemplate.getForEntity(anyString(), any()))
                .thenReturn(new ResponseEntity<>(authenticateDTO, HttpStatus.OK));

        final Optional<AuthenticateDTO> result = authenticationRequestServiceUnderTest.getAuthenticationStatus(0L);
        assertTrue(result.isPresent());
        assertEquals(result.get(), authenticateDTO);
    }

    @Test
    void getAuthenticationStatusFailure() {
        when(mockRestTemplate.getForEntity(anyString(), any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        final Optional<AuthenticateDTO> result = authenticationRequestServiceUnderTest.getAuthenticationStatus(0L);
        assertTrue(result.isEmpty());
    }

}
