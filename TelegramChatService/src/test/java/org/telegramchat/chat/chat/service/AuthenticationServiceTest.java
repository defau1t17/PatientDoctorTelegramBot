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
import org.telegramchat.chat.entity.TelegramBotAuthentication;
import org.telegramchat.chat.repository.TelegramBotAuthenticationRepository;
import org.telegramchat.chat.service.AuthenticationService;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

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

    @Mock
    private TelegramBotAuthenticationRepository mockRepository;

    private AuthenticationService authenticationServiceUnderTest;

    private final long CHAT_ID = 12213L;

    @BeforeEach
    void setUp() {
        authenticationServiceUnderTest = new AuthenticationService(mockRepository);
    }

    @Test
    void testFindAll() {
        final TelegramBotAuthentication telegramBotAuthentication = new TelegramBotAuthentication();
        final Page<TelegramBotAuthentication> telegramBotAuthentications = new PageImpl<>(
                List.of(telegramBotAuthentication));
        when(mockRepository.findAll(PageRequest.of(0, 1))).thenReturn(telegramBotAuthentications);
        final Page<TelegramBotAuthentication> result = authenticationServiceUnderTest.findAll(0, 1);
        assertEquals(result, telegramBotAuthentications);
    }


    @Test
    void testFindByChatID() {
        final TelegramBotAuthentication telegramBotAuthentication = new TelegramBotAuthentication().updateChatId(CHAT_ID);
        final Optional<TelegramBotAuthentication> expectedResult = of(telegramBotAuthentication);
        when(mockRepository.findTelegramBotAuthenticationByChatID(CHAT_ID)).thenReturn(of(telegramBotAuthentication));
        final Optional<TelegramBotAuthentication> result = authenticationServiceUnderTest.findByChatID(CHAT_ID);
        assertEquals(expectedResult, result);
    }

    @Test
    void testFindByChatID_TelegramBotAuthenticationRepositoryReturnsAbsent() {
        when(mockRepository.findTelegramBotAuthenticationByChatID(CHAT_ID)).thenReturn(empty());
        final Optional<TelegramBotAuthentication> result = authenticationServiceUnderTest.findByChatID(CHAT_ID);
        assertEquals(empty(), result);
    }

    @Test
    void testCreate() {
        TelegramBotAuthentication expected = new TelegramBotAuthentication();
        TelegramBotAuthentication experimental = new TelegramBotAuthentication();
        when(authenticationServiceUnderTest.create(experimental)).thenReturn(expected);
        final TelegramBotAuthentication result = authenticationServiceUnderTest.create(experimental);
        assertEquals(expected, result);
    }

    @Test
    void testFindAuthenticationByToken() {
        final TelegramBotAuthentication telegramBotAuthentication = new TelegramBotAuthentication();
        final Optional<TelegramBotAuthentication> expectedResult = of(telegramBotAuthentication);
        when(mockRepository.findTelegramBotAuthenticationByToken("token")).thenReturn(expectedResult);
        final Optional<TelegramBotAuthentication> result = authenticationServiceUnderTest.findAuthenticationByToken(
                "token");
        assertEquals(expectedResult, result);
    }

    @Test
    void testFindAuthenticationByToken_TelegramBotAuthenticationRepositoryReturnsAbsent() {
        when(mockRepository.findTelegramBotAuthenticationByToken("token")).thenReturn(empty());
        final Optional<TelegramBotAuthentication> result = authenticationServiceUnderTest.findAuthenticationByToken(
                "token");
        assertEquals(empty(), result);
    }

    @Test
    void testUpdate() {
        final TelegramBotAuthentication experimental = new TelegramBotAuthentication();
        final TelegramBotAuthentication expected = new TelegramBotAuthentication().updateChatId(CHAT_ID);
        when(mockRepository.save(experimental)).thenReturn(expected);
        experimental.updateChatId(CHAT_ID);
        experimental.setToken(expected.getToken());
        final TelegramBotAuthentication result = authenticationServiceUnderTest.update(experimental);
        assertEquals(experimental, result);
    }

    @Test
    void testDelete() {
        final TelegramBotAuthentication entity = new TelegramBotAuthentication();
        authenticationServiceUnderTest.delete(entity);
        verify(mockRepository).delete(entity);
    }

    @Test
    void testDeleteByChatID() {
        authenticationServiceUnderTest.deleteByChatID(CHAT_ID);
        verify(mockRepository).deleteTelegramBotAuthenticationByChatID(CHAT_ID);
    }

    @Test
    void testValidateBeforeSave() {
        final TelegramBotAuthentication object = new TelegramBotAuthentication().updateChatId(CHAT_ID);
        when(mockRepository.findTelegramBotAuthenticationByChatID(CHAT_ID)).thenReturn(Optional.empty());
        final boolean result = authenticationServiceUnderTest.validateBeforeSave(object);
        assertTrue(result);
    }

    @Test
    void testValidateBeforeSaveFailure() {
        final TelegramBotAuthentication object = new TelegramBotAuthentication().updateChatId(CHAT_ID);
        when(mockRepository.findTelegramBotAuthenticationByChatID(CHAT_ID)).thenReturn(Optional.of(object));
        final boolean result = authenticationServiceUnderTest.validateBeforeSave(object);
        assertFalse(result);
    }

    @Test
    void testAuthenticate() {
        final TelegramBotAuthentication telegramBotAuthentication = new TelegramBotAuthentication();
        final TelegramBotAuthentication telegramBotAuthentication1 = new TelegramBotAuthentication().updateChatId(CHAT_ID);
        when(mockRepository.save(telegramBotAuthentication)).thenReturn(telegramBotAuthentication1);

        final TelegramBotAuthentication result = authenticationServiceUnderTest.authenticate(CHAT_ID, telegramBotAuthentication);

        assertEquals(telegramBotAuthentication1, result);
    }
}
