package org.patientbot.patienttelegrambot.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.patientbot.patienttelegrambot.dtos.AuthenticateDTO;
import org.patientbot.patienttelegrambot.dtos.ChatStateDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MainRequestServiceTest {

    @Mock
    private RestTemplate mockRestTemplate;

    private MainRequestService mainRequestServiceUnderTest;

    @BeforeEach
    void setUp() {
        mainRequestServiceUnderTest = new MainRequestService(mockRestTemplate);
    }

    @Test
    void testGetChatState() {
        ChatStateDTO spy = spy(ChatStateDTO.class);
        when(mockRestTemplate.getForEntity(anyString(), any()))
                .thenReturn(new ResponseEntity<>(spy, HttpStatus.OK));
        final Optional<ChatStateDTO> result = mainRequestServiceUnderTest.getChatState(0);
        assertTrue(result.isPresent());
        assertEquals(spy, result.get());
    }

    @Test
    void testGetChatStateNotFound() {
        when(mockRestTemplate.getForEntity(anyString(), any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        final Optional<ChatStateDTO> result = mainRequestServiceUnderTest.getChatState(0);
        assertTrue(result.isEmpty());
    }

    @Test
    void testCreateChatState() {
        ChatStateDTO spy = spy(ChatStateDTO.class);
        when(mockRestTemplate.postForEntity(anyString(), any(), any()))
                .thenReturn(new ResponseEntity<>(spy, HttpStatus.OK));
        final Optional<ChatStateDTO> result = mainRequestServiceUnderTest.createChatState(0L);
        assertTrue(result.isPresent());
        assertEquals(spy, result.get());
    }

    @Test
    void testCreateChatState_RestTemplateThrowsRestClientException() {
        when(mockRestTemplate.getForEntity(anyString(), any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        final Optional<ChatStateDTO> result = mainRequestServiceUnderTest.getChatState(0);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAuthenticationStatus() {
        AuthenticateDTO spy = spy(AuthenticateDTO.class);
        when(mockRestTemplate.getForEntity(anyString(), any()))
                .thenReturn(new ResponseEntity<>(spy, HttpStatus.OK));
        final Optional<AuthenticateDTO> result = mainRequestServiceUnderTest.getAuthenticationStatus(0L);
        assertTrue(result.isPresent());
        assertEquals(spy, result.get());
    }

    @Test
    void testGetAuthenticationStatusNotFound() {
        when(mockRestTemplate.getForEntity(anyString(), any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        final Optional<AuthenticateDTO> result = mainRequestServiceUnderTest.getAuthenticationStatus(0L);
        assertTrue(result.isEmpty());
    }
}
