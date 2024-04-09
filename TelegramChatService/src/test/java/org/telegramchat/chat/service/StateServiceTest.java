package org.telegramchat.chat.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.telegramchat.chat.entity.ChatState;
import org.telegramchat.chat.repository.ChatStateRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@ExtendWith(MockitoExtension.class)
class StateServiceTest {

    @Mock
    private ChatStateRepository mockRepository;

    private StateService stateServiceUnderTest;

    private ChatState chatState;

    @BeforeEach
    void setUp() {
        stateServiceUnderTest = new StateService(mockRepository);
        chatState = spy();
    }

    @Test
    void testFindAll() {
        final Page<ChatState> chatStates = new PageImpl<>(List.of(spy(ChatState.class)));
        when(mockRepository.findAll(PageRequest.of(0, 1))).thenReturn(chatStates);
        final Page<ChatState> result = stateServiceUnderTest.findAll(0, 1);
        assertEquals(result.getContent().size(), 1);
    }


    @Test
    void testFindByChatID() {
        when(mockRepository.findChatStateByChatID(anyLong())).thenReturn(Optional.of(chatState));
        final Optional<ChatState> result = stateServiceUnderTest.findByChatID(anyLong());
        assertTrue(result.isPresent());
        assertEquals(chatState, result.get());
    }

    @Test
    void testFindByChatID_ChatStateRepositoryReturnsAbsent() {
        when(mockRepository.findChatStateByChatID(anyLong())).thenReturn(Optional.empty());
        final Optional<ChatState> result = stateServiceUnderTest.findByChatID(anyLong());
        assertEquals(Optional.empty(), result);
    }

    @Test
    void testCreate() {
        when(mockRepository.save(any())).thenReturn(chatState);
        final ChatState result = stateServiceUnderTest.create(spy(ChatState.class));
        assertEquals(chatState, result);
    }

    @Test
    void testUpdate() {
        when(mockRepository.save(any())).thenReturn(chatState);
        final ChatState result = stateServiceUnderTest.create(spy(ChatState.class));
        assertEquals(chatState, result);
    }

    @Test
    void testDelete() {
        stateServiceUnderTest.delete(spy(ChatState.class));
        verify(mockRepository).delete(any());
    }

    @Test
    void testDeleteByChatID() {
        stateServiceUnderTest.deleteByChatID(anyLong());
        verify(mockRepository).deleteChatStateByChatID(anyLong());
    }

    @Test
    void testValidateBeforeSave() {
        when(mockRepository.findChatStateByChatID(anyLong())).thenReturn(Optional.empty());
        final boolean result = stateServiceUnderTest.validateBeforeSave(chatState);
        assertTrue(result);
    }

    @Test
    void testValidateBeforeSaveFailure() {
        when(mockRepository.findChatStateByChatID(anyLong())).thenReturn(Optional.of(chatState));
        final boolean result = stateServiceUnderTest.validateBeforeSave(chatState);
        assertFalse(result);
    }

}
