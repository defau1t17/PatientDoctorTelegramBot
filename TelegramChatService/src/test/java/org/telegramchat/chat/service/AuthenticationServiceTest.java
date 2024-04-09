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
import org.telegramchat.chat.entity.TelegramBotAuthentication;
import org.telegramchat.chat.repository.TelegramBotAuthenticationRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@RunWith(SpringRunner.class)
@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private TelegramBotAuthenticationRepository mockRepository;

    private AuthenticationService authenticationServiceUnderTest;

    private TelegramBotAuthentication telegramBotAuthentication;



    @BeforeEach
    void setUp() {
        authenticationServiceUnderTest = new AuthenticationService(mockRepository);
        telegramBotAuthentication = spy(TelegramBotAuthentication.class);

    }

    @Test
    void testFindAll() {
        final Page<TelegramBotAuthentication> telegramBotAuthentications = new PageImpl<>(List.of(telegramBotAuthentication));
        when(mockRepository.findAll(PageRequest.of(0, 1))).thenReturn(telegramBotAuthentications);
        final Page<TelegramBotAuthentication> result = authenticationServiceUnderTest.findAll(0, 1);
        assertEquals(result.getContent().size(), 1);
        assertEquals(result.getContent().get(0), telegramBotAuthentication);
    }

    @Test
    void testFindAll_TelegramBotAuthenticationRepositoryReturnsNoItems() {
        when(mockRepository.findAll(PageRequest.of(0, 1))).thenReturn(new PageImpl<>(Collections.emptyList()));
        final Page<TelegramBotAuthentication> result = authenticationServiceUnderTest.findAll(0, 1);
        assertNotNull(result.getContent());
    }

    @Test
    void testFindByChatID() {
        when(mockRepository.findTelegramBotAuthenticationByChatID(anyLong())).thenReturn(Optional.of(telegramBotAuthentication));
        final Optional<TelegramBotAuthentication> result = authenticationServiceUnderTest.findByChatID(anyLong());
        assertTrue(result.isPresent());
        assertEquals(telegramBotAuthentication, result.get());
    }

    @Test
    void testFindByChatID_TelegramBotAuthenticationRepositoryReturnsAbsent() {
        when(mockRepository.findTelegramBotAuthenticationByChatID(anyLong())).thenReturn(Optional.empty());
        final Optional<TelegramBotAuthentication> result = authenticationServiceUnderTest.findByChatID(anyLong());
        assertEquals(Optional.empty(), result);
    }

    @Test
    void testCreate() {
        when(mockRepository.save(any())).thenReturn(telegramBotAuthentication);
        final TelegramBotAuthentication result = authenticationServiceUnderTest.create(spy(TelegramBotAuthentication.class));
        assertEquals(telegramBotAuthentication, result);
    }

    @Test
    void testFindAuthenticationByToken() {
        when(mockRepository.findTelegramBotAuthenticationByToken(anyString())).thenReturn(Optional.of(telegramBotAuthentication));
        final Optional<TelegramBotAuthentication> result = authenticationServiceUnderTest.findAuthenticationByToken(anyString());
        assertTrue(result.isPresent());
        assertEquals(telegramBotAuthentication, result.get());
    }

    @Test
    void testFindAuthenticationByToken_TelegramBotAuthenticationRepositoryReturnsAbsent() {
        when(mockRepository.findTelegramBotAuthenticationByToken(anyString())).thenReturn(Optional.empty());
        final Optional<TelegramBotAuthentication> result = authenticationServiceUnderTest.findAuthenticationByToken(anyString());
        assertEquals(Optional.empty(), result);
    }

    @Test
    void testUpdate() {
        when(mockRepository.save(any())).thenReturn(telegramBotAuthentication);
        final TelegramBotAuthentication result = authenticationServiceUnderTest.create(spy(TelegramBotAuthentication.class));
        assertEquals(telegramBotAuthentication, result);
    }

    @Test
    void testDelete() {
        authenticationServiceUnderTest.delete(spy(TelegramBotAuthentication.class));
        verify(mockRepository).delete(any());
    }

    @Test
    void testDeleteByChatID() {
        authenticationServiceUnderTest.deleteByChatID(anyLong());
        verify(mockRepository).deleteTelegramBotAuthenticationByChatID(anyLong());
    }

    @Test
    void testValidateBeforeSave() {
        when(mockRepository.findTelegramBotAuthenticationByChatID(anyLong())).thenReturn(Optional.empty());
        final boolean result = authenticationServiceUnderTest.validateBeforeSave(spy(TelegramBotAuthentication.class));
        assertTrue(result);
    }

    @Test
    void testValidateBeforeSaveFailure() {
        when(mockRepository.findTelegramBotAuthenticationByChatID(anyLong())).thenReturn(Optional.of(telegramBotAuthentication));
        final boolean result = authenticationServiceUnderTest.validateBeforeSave(spy(TelegramBotAuthentication.class));
        assertFalse(result);
    }

    @Test
    void testAuthenticate() {
        when(mockRepository.save(any())).thenReturn(telegramBotAuthentication);
        final TelegramBotAuthentication result = authenticationServiceUnderTest.authenticate(0L, telegramBotAuthentication);
        assertEquals(telegramBotAuthentication, result);
        assertEquals(telegramBotAuthentication.getChatID(), 0L);
    }
}
