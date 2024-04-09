package org.patientbot.patienttelegrambot.patientTelegramBotCommandsHandlers;

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
import org.patientbot.patienttelegrambot.stateResolvers.AuthenticationStateResolver;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationCommandHandlerTest {

    @Mock
    private ChatStateRequestService chatStateRequestService;

    @Mock
    private AuthenticationStateResolver authenticationStateResolver;

    private AuthenticationCommandHandler authenticationCommandHandlerUnderTest;

    private Update update;


    private ChatState chatState;

    private Message message;

    @BeforeEach
    void setUp() {
        authenticationCommandHandlerUnderTest = new AuthenticationCommandHandler(chatStateRequestService, authenticationStateResolver);
        update = new Update();
        chatState = new ChatState();
        message = new Message();
    }


    @Test
    void testSendResponseSuccess() {

        message.setText("/authenticate");
        message.setChat(new Chat(133L, "Long"));
        update.setMessage(message);

        when(chatStateRequestService.getChatState(anyLong())).thenReturn(Optional.of(chatState));
        when(authenticationStateResolver.responseOnState(any(), any())).thenReturn(new SendMessage("1L", "Hello World"));

        SendMessage result = authenticationCommandHandlerUnderTest.sendResponse(update);

        assertNotEquals(result, null);
        assertFalse(result.getText().isEmpty());
        assertNotEquals(result.getText(), TelegramBotResponses.SOME_ERROR);
        assertNotEquals(result.getText(), TelegramBotResponses.SYNTAX_ERROR);
    }

    @Test
    void testSendResponseFailure() {

        message.setText("/authenticate");
        message.setChat(new Chat(133L, "Long"));
        update.setMessage(message);

        when(chatStateRequestService.getChatState(anyLong())).thenReturn(Optional.of(chatState));
        when(authenticationStateResolver.responseOnState(any(), any())).thenReturn(new SendMessage("1L", TelegramBotResponses.SOME_ERROR.getDescription()));
        SendMessage result = authenticationCommandHandlerUnderTest.sendResponse(update);
        assertNotEquals(result, null);
        assertFalse(result.getText().isEmpty());
        assertEquals(result.getText(), TelegramBotResponses.SOME_ERROR.getDescription());

    }


}
