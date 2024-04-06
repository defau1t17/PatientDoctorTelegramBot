package org.telegrambots.doctortelegrambot.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.telegrambots.doctortelegrambot.dto.AuthenticateDTO;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationRequestServiceTest {

    @Mock
    private RestTemplate mockRestTemplate;

    private AuthenticationRequestService authenticationRequestServiceUnderTest;

    private AuthenticateDTO authenticateDTO;

    @BeforeEach
    void setUp() {
        authenticateDTO = new AuthenticateDTO(1L, "some-token");
        authenticationRequestServiceUnderTest = new AuthenticationRequestService(mockRestTemplate);
    }

    @Test
    void testAuthenticate() {

        when(mockRestTemplate.exchange(anyString(), eq(HttpMethod.PATCH), any(),
                eq(AuthenticateDTO.class))).thenReturn(new ResponseEntity<>(authenticateDTO, HttpStatus.OK));

        final Optional<AuthenticateDTO> result = authenticationRequestServiceUnderTest.authenticate(0L, "token");
        assertTrue(result.isPresent());
        assertEquals(authenticateDTO, result.get());
    }

    @Test
    void testAuthenticateAuthenticationError() {
        when(mockRestTemplate.exchange(anyString(), eq(HttpMethod.PATCH), any(),
                eq(AuthenticateDTO.class))).thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

        final Optional<AuthenticateDTO> result = authenticationRequestServiceUnderTest.authenticate(0L, "token");
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAuthenticationStatusSuccess() {
        when(mockRestTemplate.getForEntity(anyString(), eq(AuthenticateDTO.class)))
                .thenReturn(new ResponseEntity<>(authenticateDTO, HttpStatus.OK));

        final Optional<AuthenticateDTO> result = authenticationRequestServiceUnderTest.getAuthenticationStatus(0L);
        assertTrue(result.isPresent());
        assertEquals(authenticateDTO, result.get());
    }

    @Test
    void testGetAuthenticationStatusNotFound() {
        when(mockRestTemplate.getForEntity(anyString(), eq(AuthenticateDTO.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        final Optional<AuthenticateDTO> result = authenticationRequestServiceUnderTest.getAuthenticationStatus(0L);
        assertTrue(result.isEmpty());
    }
}
