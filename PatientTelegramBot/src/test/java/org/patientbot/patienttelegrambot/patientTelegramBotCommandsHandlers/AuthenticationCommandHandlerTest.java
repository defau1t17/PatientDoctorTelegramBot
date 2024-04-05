package org.patientbot.patienttelegrambot.patientTelegramBotCommandsHandlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.patientbot.patienttelegrambot.dtos.AuthenticateDTO;
import org.patientbot.patienttelegrambot.dtos.AuthenticatedUserDTO;
import org.patientbot.patienttelegrambot.dtos.ChatStateDTO;
import org.patientbot.patienttelegrambot.entity.ChatStates;
import org.patientbot.patienttelegrambot.entity.TelegramBotResponses;
import org.patientbot.patienttelegrambot.services.AuthenticationRequestService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationCommandHandlerTest {

    @Mock
    private AuthenticationRequestService mockAuthenticationRequestService;

    private AuthenticationCommandHandler authenticationCommandHandlerUnderTest;

    private Update update;


    private ChatStateDTO chatState;

    @BeforeEach
    void setUp() {
        authenticationCommandHandlerUnderTest = new AuthenticationCommandHandler(mockAuthenticationRequestService);
        update = new Update();

    }


    @Test
    void testSendResponseWhenChatStateIsDefault() {

        chatState = new ChatStateDTO(ChatStates.DEFAULT.name(), 0L);
        Message message = new Message();
        message.setText("/authenticate");
        message.setChat(new Chat(133L, "Long"));
        update.setMessage(message);

        when(mockAuthenticationRequestService.getChatState(anyLong())).thenReturn(Optional.of(chatState));
        when(mockAuthenticationRequestService.updateChatState(anyLong(), any())).thenReturn(Optional.of(chatState));

        SendMessage result = authenticationCommandHandlerUnderTest.sendResponse(update);
        assertEquals(result.getText(), TelegramBotResponses.INPUT_TOKEN_DESCRIPTION.getDescription());

    }

    @Test
    void testSendResponseSomeErrorBecauseOfChatState() {

        Message message = new Message();
        message.setText("/authenticate");
        message.setChat(new Chat(133L, "Long"));
        update.setMessage(message);

        when(mockAuthenticationRequestService.getChatState(anyLong())).thenReturn(Optional.empty());

        SendMessage result = authenticationCommandHandlerUnderTest.sendResponse(update);
        assertEquals(result.getText(), TelegramBotResponses.SOME_ERROR.getDescription());

    }


    @Test
    void testSendResponseWhenChatStateIsWaitingForToken() {

        chatState = new ChatStateDTO(ChatStates.WAITING_FOR_TOKEN.name(), 0L);
        AuthenticateDTO authenticateDTO = spy(AuthenticateDTO.class);
        AuthenticatedUserDTO authenticatedUserDTO = spy(AuthenticatedUserDTO.class);
        Message message = new Message();
        message.setText("some-token");
        message.setChat(new Chat(133L, "Long"));
        update.setMessage(message);

        when(mockAuthenticationRequestService.getChatState(anyLong())).thenReturn(Optional.of(chatState));
        when(mockAuthenticationRequestService.authenticate(anyLong(), anyString())).thenReturn(Optional.of(authenticateDTO));
        when(mockAuthenticationRequestService.updateChatIDInHospitalDatabase(anyLong(), anyString())).thenReturn(Optional.of(authenticatedUserDTO));

        SendMessage result = authenticationCommandHandlerUnderTest.sendResponse(update);
        assertEquals(result.getText(), "%s\nWelcome %s %s"
                .formatted(TelegramBotResponses.AUTH_PASSED.getDescription(), authenticatedUserDTO.getName(), authenticatedUserDTO.getSecondName()));

        verify(mockAuthenticationRequestService).moveChatStateToNextState(anyLong());

    }


    @Test
    void testSendResponseWhenChatStateIsWaitingForTokenFailureBecauseOfWrongToken() {

        chatState = new ChatStateDTO(ChatStates.WAITING_FOR_TOKEN.name(), 0L);
        AuthenticatedUserDTO authenticatedUserDTO = spy(AuthenticatedUserDTO.class);
        Message message = new Message();
        message.setText("some-token");
        message.setChat(new Chat(133L, "Long"));
        update.setMessage(message);

        when(mockAuthenticationRequestService.getChatState(anyLong())).thenReturn(Optional.of(chatState));
        when(mockAuthenticationRequestService.authenticate(anyLong(), anyString())).thenReturn(Optional.empty());
        when(mockAuthenticationRequestService.updateChatIDInHospitalDatabase(anyLong(), anyString())).thenReturn(Optional.of(authenticatedUserDTO));

        SendMessage result = authenticationCommandHandlerUnderTest.sendResponse(update);
        assertEquals(result.getText(), TelegramBotResponses.BAD_CREDS.getDescription());
    }


    @Test
    void testResponseOnStateWhenStateIsDefault() {
        chatState = new ChatStateDTO(ChatStates.DEFAULT.name(), 0L);
        Message message = new Message();
        message.setText("some-token");
        message.setChat(new Chat(133L, "Long"));
        update.setMessage(message);
        when(mockAuthenticationRequestService.updateChatState(anyLong(), any())).thenReturn(Optional.of(chatState));
        authenticationCommandHandlerUnderTest.responseOnState(ChatStates.valueOf(chatState.getChatStates()), update);
        try {
            Field responseMessageField = authenticationCommandHandlerUnderTest.getClass().getDeclaredField("responseMessage");
            responseMessageField.setAccessible(true);
            String responseMessage = (String) responseMessageField.get(authenticationCommandHandlerUnderTest);
            assertEquals(responseMessage, TelegramBotResponses.INPUT_TOKEN_DESCRIPTION.getDescription());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testResponseOnStateWhenStateIsDefaultAndSomeErrorHappened() {
        chatState = new ChatStateDTO(ChatStates.DEFAULT.name(), 0L);
        Message message = new Message();
        message.setText("some-token");
        message.setChat(new Chat(133L, "Long"));
        update.setMessage(message);
        when(mockAuthenticationRequestService.updateChatState(anyLong(), any())).thenReturn(Optional.empty());
        authenticationCommandHandlerUnderTest.responseOnState(ChatStates.valueOf(chatState.getChatStates()), update);
        try {
            Field responseMessageField = authenticationCommandHandlerUnderTest.getClass().getDeclaredField("responseMessage");
            responseMessageField.setAccessible(true);
            String responseMessage = (String) responseMessageField.get(authenticationCommandHandlerUnderTest);
            assertEquals(responseMessage, TelegramBotResponses.SOME_ERROR.getDescription());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void testResponseOnStateWhenStateIsWaitingForToken() {
        chatState = new ChatStateDTO(ChatStates.WAITING_FOR_TOKEN.name(), 0L);
        AuthenticateDTO authenticateDTO = spy(AuthenticateDTO.class);
        AuthenticatedUserDTO authenticatedUserDTO = spy(AuthenticatedUserDTO.class);
        Message message = new Message();
        message.setText("some-token");
        message.setChat(new Chat(133L, "Long"));
        update.setMessage(message);

        when(mockAuthenticationRequestService.authenticate(anyLong(), anyString())).thenReturn(Optional.of(authenticateDTO));
        when(mockAuthenticationRequestService.updateChatIDInHospitalDatabase(anyLong(), anyString())).thenReturn(Optional.of(authenticatedUserDTO));

        authenticationCommandHandlerUnderTest.responseOnState(ChatStates.valueOf(chatState.getChatStates()), update);

        try {
            Field responseMessageField = authenticationCommandHandlerUnderTest.getClass().getDeclaredField("responseMessage");
            responseMessageField.setAccessible(true);
            String responseMessage = (String) responseMessageField.get(authenticationCommandHandlerUnderTest);
            assertEquals(responseMessage, "%s\nWelcome %s %s"
                    .formatted(TelegramBotResponses.AUTH_PASSED.getDescription(), authenticatedUserDTO.getName(), authenticatedUserDTO.getSecondName()));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        verify(mockAuthenticationRequestService).moveChatStateToNextState(anyLong());

    }

    @Test
    void testResponseOnStateWhenStateIsWaitingForTokenAndErrorHappened() {
        chatState = new ChatStateDTO(ChatStates.WAITING_FOR_TOKEN.name(), 0L);
        AuthenticateDTO authenticateDTO = spy(AuthenticateDTO.class);
        AuthenticatedUserDTO authenticatedUserDTO = spy(AuthenticatedUserDTO.class);
        Message message = new Message();
        message.setText("some-token");
        message.setChat(new Chat(133L, "Long"));
        update.setMessage(message);

        when(mockAuthenticationRequestService.authenticate(anyLong(), anyString())).thenReturn(Optional.empty());
        when(mockAuthenticationRequestService.updateChatIDInHospitalDatabase(anyLong(), anyString())).thenReturn(Optional.of(authenticatedUserDTO));

        authenticationCommandHandlerUnderTest.responseOnState(ChatStates.valueOf(chatState.getChatStates()), update);

        try {
            Field responseMessageField = authenticationCommandHandlerUnderTest.getClass().getDeclaredField("responseMessage");
            responseMessageField.setAccessible(true);
            String responseMessage = (String) responseMessageField.get(authenticationCommandHandlerUnderTest);
            assertEquals(responseMessage, TelegramBotResponses.BAD_CREDS.getDescription());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void testMoveChatState() {
        authenticationCommandHandlerUnderTest.moveChatState(anyLong());
        verify(mockAuthenticationRequestService).moveChatStateToNextState(anyLong());
    }
}
