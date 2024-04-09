package org.patientbot.patienttelegrambot.stateResolvers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.patientbot.patienttelegrambot.dtos.AuthenticateDTO;
import org.patientbot.patienttelegrambot.dtos.AuthenticatedUserDTO;
import org.patientbot.patienttelegrambot.dtos.ChatStateDTO;
import org.patientbot.patienttelegrambot.entity.ChatState;
import org.patientbot.patienttelegrambot.entity.ChatStates;
import org.patientbot.patienttelegrambot.entity.TelegramBotResponses;
import org.patientbot.patienttelegrambot.services.AuthenticationRequestService;
import org.patientbot.patienttelegrambot.services.ChatStateRequestService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationStateResolverTest {


    @Mock
    private ChatStateRequestService chatStateRequestService;

    @Mock
    private AuthenticationRequestService authenticationRequestService;

    private AuthenticationStateResolver authenticationStateResolverUnderTest;

    private Update update;

    private ChatState chatState;

    private Message message;

    @BeforeEach
    void setUp() {
        update = new Update();
        message = new Message();
        chatState = new ChatState(1L);
        authenticationStateResolverUnderTest = new AuthenticationStateResolver(authenticationRequestService, chatStateRequestService);
    }


    @Test
    void testSendResponseWhenChatStateIsDefault() {
        message.setText("/authenticate");
        message.setChat(new Chat(1L, "Long"));
        update.setMessage(message);

        when(chatStateRequestService.updateChatState(anyLong(), any())).thenReturn(Optional.of(chatState));

        SendMessage result = authenticationStateResolverUnderTest.responseOnState(chatState, update);
        assertEquals(result.getText(), TelegramBotResponses.INPUT_TOKEN_DESCRIPTION.getDescription());

    }


    @Test
    void testSendResponseWhenChatStateIsWaitingForToken() {
        chatState.setChatStates(ChatStates.WAITING_FOR_TOKEN);
        AuthenticateDTO authenticateDTO = spy(AuthenticateDTO.class);
        AuthenticatedUserDTO authenticatedUserDTO = spy(AuthenticatedUserDTO.class);
        message.setText("some-token");
        message.setChat(new Chat(133L, "Long"));
        update.setMessage(message);

        when(authenticationRequestService.authenticate(anyLong(), anyString())).thenReturn(Optional.of(authenticateDTO));
        when(chatStateRequestService.updateChatIDInHospitalDatabase(anyLong(), anyString())).thenReturn(Optional.of(authenticatedUserDTO));

        SendMessage result = authenticationStateResolverUnderTest.responseOnState(chatState, update);
        assertEquals(result.getText(), "%s\nWelcome %s %s"
                .formatted(TelegramBotResponses.AUTH_PASSED.getDescription(), authenticatedUserDTO.getName(), authenticatedUserDTO.getSecondName()));

        verify(chatStateRequestService).moveChatStateToNextState(anyLong());

    }


    @Test
    void testSendResponseWhenChatStateIsWaitingForTokenFailureBecauseOfWrongToken() {
        chatState.setChatStates(ChatStates.WAITING_FOR_TOKEN);

        AuthenticatedUserDTO authenticatedUserDTO = spy(AuthenticatedUserDTO.class);
        message.setText("some-token");
        message.setChat(new Chat(133L, "Long"));
        update.setMessage(message);

        when(authenticationRequestService.authenticate(anyLong(), anyString())).thenReturn(Optional.empty());
        when(chatStateRequestService.updateChatIDInHospitalDatabase(anyLong(), anyString())).thenReturn(Optional.of(authenticatedUserDTO));

        SendMessage result = authenticationStateResolverUnderTest.responseOnState(chatState, update);
        assertEquals(result.getText(), TelegramBotResponses.BAD_CREDS.getDescription());
    }


    @Test
    void testResponseOnStateWhenStateIsDefault() {
        message.setText("some-token");
        message.setChat(new Chat(133L, "Long"));
        update.setMessage(message);

        when(chatStateRequestService.updateChatState(anyLong(), any())).thenReturn(Optional.of(chatState));

        authenticationStateResolverUnderTest.responseOnState(chatState, update);

        try {
            Field responseMessageField = authenticationStateResolverUnderTest.getClass().getDeclaredField("responseMessage");
            responseMessageField.setAccessible(true);
            String responseMessage = (String) responseMessageField.get(authenticationStateResolverUnderTest);
            assertEquals(responseMessage, TelegramBotResponses.INPUT_TOKEN_DESCRIPTION.getDescription());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testResponseOnStateWhenStateIsDefaultAndSomeErrorHappened() {

        message.setText("some-token");
        message.setChat(new Chat(133L, "Long"));
        update.setMessage(message);

        when(chatStateRequestService.updateChatState(anyLong(), any())).thenReturn(Optional.empty());
        authenticationStateResolverUnderTest.responseOnState(chatState, update);

        try {
            Field responseMessageField = authenticationStateResolverUnderTest.getClass().getDeclaredField("responseMessage");
            responseMessageField.setAccessible(true);
            String responseMessage = (String) responseMessageField.get(authenticationStateResolverUnderTest);
            assertEquals(responseMessage, TelegramBotResponses.SOME_ERROR.getDescription());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void testResponseOnStateWhenStateIsWaitingForToken() {
        chatState.setChatStates(ChatStates.WAITING_FOR_TOKEN);
        AuthenticateDTO authenticateDTO = spy(AuthenticateDTO.class);
        AuthenticatedUserDTO authenticatedUserDTO = spy(AuthenticatedUserDTO.class);
        message.setText("some-token");
        message.setChat(new Chat(133L, "Long"));
        update.setMessage(message);

        when(authenticationRequestService.authenticate(anyLong(), anyString())).thenReturn(Optional.of(authenticateDTO));
        when(chatStateRequestService.updateChatIDInHospitalDatabase(anyLong(), anyString())).thenReturn(Optional.of(authenticatedUserDTO));

        authenticationStateResolverUnderTest.responseOnState(chatState, update);

        try {
            Field responseMessageField = authenticationStateResolverUnderTest.getClass().getDeclaredField("responseMessage");
            responseMessageField.setAccessible(true);
            String responseMessage = (String) responseMessageField.get(authenticationStateResolverUnderTest);
            assertEquals(responseMessage, "%s\nWelcome %s %s"
                    .formatted(TelegramBotResponses.AUTH_PASSED.getDescription(), authenticatedUserDTO.getName(), authenticatedUserDTO.getSecondName()));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        verify(chatStateRequestService).moveChatStateToNextState(anyLong());

    }

    @Test
    void testResponseOnStateWhenStateIsWaitingForTokenAndErrorHappened() {
        chatState.setChatStates(ChatStates.WAITING_FOR_TOKEN);
        AuthenticateDTO authenticateDTO = spy(AuthenticateDTO.class);
        AuthenticatedUserDTO authenticatedUserDTO = spy(AuthenticatedUserDTO.class);
        Message message = new Message();
        message.setText("some-token");
        message.setChat(new Chat(133L, "Long"));
        update.setMessage(message);

        when(authenticationRequestService.authenticate(anyLong(), anyString())).thenReturn(Optional.empty());
        when(chatStateRequestService.updateChatIDInHospitalDatabase(anyLong(), anyString())).thenReturn(Optional.of(authenticatedUserDTO));

        authenticationStateResolverUnderTest.responseOnState(chatState, update);

        try {
            Field responseMessageField = authenticationStateResolverUnderTest.getClass().getDeclaredField("responseMessage");
            responseMessageField.setAccessible(true);
            String responseMessage = (String) responseMessageField.get(authenticationStateResolverUnderTest);
            assertEquals(responseMessage, TelegramBotResponses.BAD_CREDS.getDescription());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
