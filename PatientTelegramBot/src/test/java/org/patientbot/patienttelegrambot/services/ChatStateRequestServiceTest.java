package org.patientbot.patienttelegrambot.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.patientbot.patienttelegrambot.dtos.AuthenticatedUserDTO;
import org.patientbot.patienttelegrambot.entity.ChatState;
import org.patientbot.patienttelegrambot.entity.ChatStates;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatStateRequestServiceTest {

    @Mock
    private RestTemplate mockRestTemplate;

    private ChatStateRequestService chatStateRequestServiceUnderTest;


    private ChatState chatState;

    @BeforeEach
    void setUp() {
        chatState = new ChatState(1L);
        chatStateRequestServiceUnderTest = new ChatStateRequestService(mockRestTemplate);
    }

    @Test
    void testGetChatStateSuccess() {
        when(mockRestTemplate.getForEntity(anyString(), any()))
                .thenReturn(new ResponseEntity<>(chatState, HttpStatus.OK));

        final Optional<ChatState> result = chatStateRequestServiceUnderTest.getChatState(0L);

        assertTrue(result.isPresent());
        assertEquals(chatState, result.get());
    }


    @Test
    void testGetChatStateNotFound() {
        when(mockRestTemplate.getForEntity(anyString(), any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        final Optional<ChatState> result = chatStateRequestServiceUnderTest.getChatState(0L);
        assertTrue(result.isEmpty());
    }

    @Test
    void testCreateChatState() {
        when(mockRestTemplate.postForEntity(anyString(), any(), any()))
                .thenReturn(new ResponseEntity<>(chatState, HttpStatus.OK));
        final Optional<ChatState> result = chatStateRequestServiceUnderTest.createChatState(0L);
        assertTrue(result.isPresent());
        assertEquals(chatState, result.get());
    }

    @Test
    void testCreateChatStateBadRequest() {
        when(mockRestTemplate.postForEntity(anyString(), any(), any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

        final Optional<ChatState> result = chatStateRequestServiceUnderTest.createChatState(0L);
        assertTrue(result.isEmpty());
    }

    @Test
    void testMoveChatStateToNextStateSuccess() {
        when(mockRestTemplate.postForEntity(anyString(), any(), any()))
                .thenReturn(new ResponseEntity<>(chatState, HttpStatus.OK));
        final Optional<ChatState> result = chatStateRequestServiceUnderTest.moveChatStateToNextState(0L);
        assertTrue(result.isPresent());
        assertEquals(chatState, result.get());
    }

    @Test
    void testMoveChatStateToNextStateFailure() {
        when(mockRestTemplate.postForEntity(anyString(), any(), any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        final Optional<ChatState> result = chatStateRequestServiceUnderTest.moveChatStateToNextState(0L);
        assertTrue(result.isEmpty());
    }

    @Test
    void testUpdateChatStateSuccess() {
        when(mockRestTemplate.postForEntity(anyString(), any(), any()))
                .thenReturn(new ResponseEntity<>(chatState, HttpStatus.OK));
        final Optional<ChatState> result = chatStateRequestServiceUnderTest.updateChatState(0L, ChatStates.DEFAULT);
        assertTrue(result.isPresent());
        assertEquals(chatState, result.get());
    }

    @Test
    void testUpdateChatStateFailure() {
        when(mockRestTemplate.postForEntity(anyString(), any(), any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        final Optional<ChatState> result = chatStateRequestServiceUnderTest.updateChatState(0L, ChatStates.DEFAULT);
        assertTrue(result.isEmpty());
    }

    @Test
    void testUpdateChatIDInHospitalDatabaseSuccess() {
        AuthenticatedUserDTO authenticatedUserDTO = spy(AuthenticatedUserDTO.class);
        when(mockRestTemplate.exchange(anyString(), eq(HttpMethod.PATCH), any(), eq(AuthenticatedUserDTO.class)))
                .thenReturn(new ResponseEntity<>(authenticatedUserDTO, HttpStatus.OK));
        final Optional<AuthenticatedUserDTO> result = chatStateRequestServiceUnderTest.updateChatIDInHospitalDatabase(0L, "some-token");
        assertTrue(result.isPresent());
        assertEquals(authenticatedUserDTO, result.get());
    }

    @Test
    void testUpdateChatIDInHospitalDatabaseFailure() {
        when(mockRestTemplate.exchange(anyString(), eq(HttpMethod.PATCH), any(), eq(AuthenticatedUserDTO.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        final Optional<AuthenticatedUserDTO> result = chatStateRequestServiceUnderTest.updateChatIDInHospitalDatabase(0L, "some-token");
        assertTrue(result.isEmpty());
    }

}
