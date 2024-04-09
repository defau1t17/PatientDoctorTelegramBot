package org.telegrambots.doctortelegrambot.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.telegrambots.doctortelegrambot.dto.AuthenticatedUserDTO;
import org.telegrambots.doctortelegrambot.entities.ChatState;
import org.telegrambots.doctortelegrambot.entities.ChatStates;

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
        chatState = spy(ChatState.class);
        chatStateRequestServiceUnderTest = new ChatStateRequestService(mockRestTemplate);
    }

    @Test
    void testGetChatState() {
        when(mockRestTemplate.getForEntity(anyString(), eq(ChatState.class)))
                .thenReturn(new ResponseEntity<>(chatState, HttpStatus.OK));
        final Optional<ChatState> result = chatStateRequestServiceUnderTest.getChatState(0L);
        assertTrue(result.isPresent());
        assertEquals(chatState, result.get());
    }

    @Test
    void testGetChatStateNotFound() {
        when(mockRestTemplate.getForEntity(anyString(), eq(ChatState.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        final Optional<ChatState> result = chatStateRequestServiceUnderTest.getChatState(0L);
        assertTrue(result.isEmpty());
    }

    @Test
    void testUpdateChatState() {
        when(mockRestTemplate.postForEntity(anyString(), any(), eq(ChatState.class)))
                .thenReturn(new ResponseEntity<>(chatState, HttpStatus.OK));
        final Optional<ChatState> result = chatStateRequestServiceUnderTest.updateChatState(0L, ChatStates.DEFAULT);
        assertTrue(result.isPresent());
        assertEquals(chatState, result.get());

    }

    @Test
    void testUpdateChatStateNotFound() {
        when(mockRestTemplate.postForEntity(anyString(), any(), eq(ChatState.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        final Optional<ChatState> result = chatStateRequestServiceUnderTest.updateChatState(0L, ChatStates.DEFAULT);
        assertTrue(result.isEmpty());
    }

    @Test
    void testCreateChatState() {
        when(mockRestTemplate.postForEntity(anyString(), any(), eq(ChatState.class)))
                .thenReturn(new ResponseEntity<>(chatState, HttpStatus.OK));
        final Optional<ChatState> result = chatStateRequestServiceUnderTest.createChatState(0L);
        assertTrue(result.isPresent());
        assertEquals(chatState, result.get());
    }

    @Test
    void testCreateChatStateFailure() {
        when(mockRestTemplate.postForEntity(anyString(), any(), eq(ChatState.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        final Optional<ChatState> result = chatStateRequestServiceUnderTest.createChatState(0L);
        assertTrue(result.isEmpty());
    }

    @Test
    void testMoveChatStateToNextState() {
        when(mockRestTemplate.postForEntity(anyString(), any(), eq(ChatState.class)))
                .thenReturn(new ResponseEntity<>(chatState, HttpStatus.OK));
        final Optional<ChatState> result = chatStateRequestServiceUnderTest.moveChatStateToNextState(0L);
        assertTrue(result.isPresent());
        assertEquals(chatState, result.get());
    }

    @Test
    void testMoveChatStateToNextStateFailure() {
        when(mockRestTemplate.postForEntity(anyString(), any(), eq(ChatState.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        final Optional<ChatState> result = chatStateRequestServiceUnderTest.moveChatStateToNextState(0L);
        assertTrue(result.isEmpty());
    }

    @Test
    void testUpdateChatIDInHospitalDatabase() {
        AuthenticatedUserDTO authenticatedUserDTO = spy(AuthenticatedUserDTO.class);
        when(mockRestTemplate.exchange(anyString(), eq(HttpMethod.PATCH), any(), eq(AuthenticatedUserDTO.class)))
                .thenReturn(new ResponseEntity<>(authenticatedUserDTO, HttpStatus.OK));
        final Optional<AuthenticatedUserDTO> result = chatStateRequestServiceUnderTest.updateChatIDInHospitalDatabase(
                0L, "some-token");
        assertTrue(result.isPresent());
        assertEquals(authenticatedUserDTO, result.get());
    }

    @Test
    void testUpdateChatIDInHospitalDatabaseFailure() {
        when(mockRestTemplate.exchange(anyString(), eq(HttpMethod.PATCH), any(), eq(AuthenticatedUserDTO.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        final Optional<AuthenticatedUserDTO> result = chatStateRequestServiceUnderTest.updateChatIDInHospitalDatabase(
                0L, "some-token");
        assertTrue(result.isEmpty());
    }

    @Test
    void testRollBackChatStateToDefault() {
        when(mockRestTemplate.postForEntity(anyString(), any(), eq(ChatState.class)))
                .thenReturn(new ResponseEntity<>(chatState, HttpStatus.OK));
        final Optional<ChatState> result = chatStateRequestServiceUnderTest.rollBackChatStateToDefault(0L);
        assertTrue(result.isPresent());
        assertEquals(chatState, result.get());
    }

    @Test
    void testRollBackChatStateToDefault_RestTemplateThrowsRestClientException() {
        when(mockRestTemplate.postForEntity(anyString(), any(), eq(ChatState.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        final Optional<ChatState> result = chatStateRequestServiceUnderTest.rollBackChatStateToDefault(0L);
        assertTrue(result.isEmpty());
    }
}
