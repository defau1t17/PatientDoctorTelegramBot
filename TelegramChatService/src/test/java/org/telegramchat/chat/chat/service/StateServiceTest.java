package org.telegramchat.chat.chat.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.telegramchat.chat.entity.ChatState;
import org.telegramchat.chat.entity.ChatStates;
import org.telegramchat.chat.repository.ChatStateRepository;
import org.telegramchat.chat.service.StateService;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StateServiceTest {


    private static PostgreSQLContainer postgresContainer;

    {
        postgresContainer = new PostgreSQLContainer("postgres:latest")
                .withDatabaseName("authentication")
                .withUsername("authentication")
                .withPassword("authentication");
        postgresContainer.start();
    }


    @DynamicPropertySource
    public static void overrideProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", postgresContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", postgresContainer::getPassword);
        dynamicPropertyRegistry.add("spring.datasource.driver-class-name", postgresContainer::getDriverClassName);
    }

    private final long CHAT_ID = 1231231l;

    @Mock
    private ChatStateRepository mockRepository;

    private StateService stateServiceUnderTest;

    @BeforeEach
    void setUp() {
        stateServiceUnderTest = new StateService(mockRepository);
    }

    @Test
    void testFindAll() {
        final Page<ChatState> chatStates = new PageImpl<>(List.of(new ChatState(CHAT_ID)));
        when(mockRepository.findAll(PageRequest.of(0, 1))).thenReturn(chatStates);
        final Page<ChatState> result = stateServiceUnderTest.findAll(0, 1);
        assertNotNull(result);
    }

    @Test
    void testFindByChatID() {
        final Optional<ChatState> expectedResult = Optional.of(new ChatState(CHAT_ID));
        when(mockRepository.findChatStateByChatID(CHAT_ID)).thenReturn(Optional.of(new ChatState(CHAT_ID)));
        final Optional<ChatState> result = stateServiceUnderTest.findByChatID(CHAT_ID);
        assertEquals(expectedResult, result);
    }

    @Test
    void testFindByChatID_ChatStateRepositoryReturnsAbsent() {
        when(mockRepository.findChatStateByChatID(0L)).thenReturn(Optional.empty());
        final Optional<ChatState> result = stateServiceUnderTest.findByChatID(0L);
        assertEquals(Optional.empty(), result);
    }

    @Test
    void testCreate() {
        final ChatState expectedResult = new ChatState(CHAT_ID);
        when(mockRepository.save(new ChatState(CHAT_ID))).thenReturn(new ChatState(CHAT_ID));
        final ChatState result = stateServiceUnderTest.create(new ChatState(CHAT_ID));
        assertEquals(expectedResult, result);
    }

    @Test
    void testUpdate() {
        final ChatState expectedResult = new ChatState(CHAT_ID);
        when(mockRepository.save(new ChatState(CHAT_ID))).thenReturn(new ChatState(CHAT_ID));
        final ChatState result = stateServiceUnderTest.update(new ChatState(CHAT_ID));
        assertEquals(expectedResult, result);
    }

    @Test
    void testDelete() {
        stateServiceUnderTest.delete(new ChatState(CHAT_ID));
        verify(mockRepository).delete(new ChatState(CHAT_ID));
    }

    @Test
    void testDeleteByChatID() {
        stateServiceUnderTest.deleteByChatID(CHAT_ID);
        verify(mockRepository).deleteChatStateByChatID(CHAT_ID);
    }

    @Test
    void testValidateBeforeSave() {
        final ChatState object = new ChatState(CHAT_ID);
        when(mockRepository.findChatStateByChatID(CHAT_ID)).thenReturn(Optional.of(new ChatState(CHAT_ID)));
        final boolean result = stateServiceUnderTest.validateBeforeSave(object);
        assertFalse(result);
    }

    @Test
    void testValidateBeforeSave_ChatStateRepositoryReturnsAbsent() {
        final ChatState object = new ChatState(CHAT_ID);
        when(mockRepository.findChatStateByChatID(CHAT_ID)).thenReturn(Optional.empty());
        final boolean result = stateServiceUnderTest.validateBeforeSave(object);
        assertTrue(result);
    }

    @Test
    void testMoveState() {
        final ChatState chatState = new ChatState(CHAT_ID);
        final ChatState expectedResult = new ChatState(CHAT_ID);
        when(mockRepository.save(new ChatState(CHAT_ID))).thenReturn(new ChatState(CHAT_ID));
        final ChatState result = stateServiceUnderTest.moveState(chatState, ChatStates.WAITING_FOR_CHAMBER_NUMBER.getCommandReference());
        assertEquals(expectedResult, result);
    }
}
