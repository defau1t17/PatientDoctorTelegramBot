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
    void testGetChatState() {
        ChatStateDTO spy = spy(ChatStateDTO.class);
        when(mockRestTemplate.getForEntity(anyString(), any()))
                .thenReturn(new ResponseEntity<>(spy, HttpStatus.OK));
        final Optional<ChatStateDTO> result = authenticationRequestServiceUnderTest.getChatState(0L);
        assertTrue(result.isPresent());
        assertEquals(spy, result.get());
    }

    @Test
    void testGetChatStateNotFound() {
        when(mockRestTemplate.getForEntity(anyString(), any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        final Optional<ChatStateDTO> result = authenticationRequestServiceUnderTest.getChatState(0L);
        assertTrue(result.isEmpty());
    }

    @Test
    void testMoveChatStateToNextState() {
        ChatStateDTO main = new ChatStateDTO(ChatStates.WAITING_FOR_DOCTOR_ID.name(), 12L);
        ChatStateDTO fake = new ChatStateDTO(ChatStates.DEFAULT.name(), 12L);

        when(mockRestTemplate.postForEntity(anyString(), any(), any()))
                .thenReturn(new ResponseEntity<>(fake, HttpStatus.OK));
        final Optional<ChatStateDTO> result = authenticationRequestServiceUnderTest.moveChatStateToNextState(0L);
        assertTrue(result.isPresent());
        assertNotEquals(main, result.get());
    }

    @Test
    void testMoveChatStateToNextStateNotFound() {
        when(mockRestTemplate.postForEntity(anyString(), any(), any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        final Optional<ChatStateDTO> result = authenticationRequestServiceUnderTest.moveChatStateToNextState(0L);
        assertTrue(result.isEmpty());
    }

    @Test
    void testUpdateChatState() {
        ChatStateDTO main = new ChatStateDTO(ChatStates.WAITING_FOR_PREVIOUS_OR_NEXT_COMMAND_DOCTORS.name(), 0L);
        ChatStateDTO fake = new ChatStateDTO(ChatStates.DEFAULT.name(), 0L);
        when(mockRestTemplate.postForEntity(anyString(), any(), any()))
                .thenReturn(new ResponseEntity<>(fake, HttpStatus.OK));
        final Optional<ChatStateDTO> result = authenticationRequestServiceUnderTest.updateChatState(0L, ChatStates.DEFAULT);
        assertTrue(result.isPresent());
        assertNotEquals(main, result.get());
    }

    @Test
    void testUpdateChatState_RestTemplateThrowsRestClientException() {
        when(mockRestTemplate.postForEntity(anyString(), any(), any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        final Optional<ChatStateDTO> result = authenticationRequestServiceUnderTest.updateChatState(0L, ChatStates.DEFAULT);
        assertTrue(result.isEmpty());
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
    void testUpdateChatIDInHospitalDatabase() {
        AuthenticatedUserDTO spy = spy(AuthenticatedUserDTO.class);
        when(mockRestTemplate.exchange(anyString(), eq(HttpMethod.PATCH), any(), eq(AuthenticatedUserDTO.class)))
                .thenReturn(new ResponseEntity<>(spy, HttpStatus.OK));
        final Optional<AuthenticatedUserDTO> result = authenticationRequestServiceUnderTest.updateChatIDInHospitalDatabase(
                0L, "token");
        assertTrue(result.isPresent());
        assertEquals(spy, result.get());
    }

    @Test
    void testUpdateChatIDInHospitalDatabase_RestTemplateThrowsRestClientException() {
        when(mockRestTemplate.exchange(anyString(), eq(HttpMethod.PATCH), any(), eq(AuthenticatedUserDTO.class)
        ))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        final Optional<AuthenticatedUserDTO> result = authenticationRequestServiceUnderTest.updateChatIDInHospitalDatabase(0L, "token");
        assertTrue(result.isEmpty());
    }

}
