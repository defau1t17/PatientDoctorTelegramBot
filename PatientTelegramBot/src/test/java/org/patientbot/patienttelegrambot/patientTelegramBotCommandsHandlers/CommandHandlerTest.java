package org.patientbot.patienttelegrambot.patientTelegramBotCommandsHandlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.patientbot.patienttelegrambot.dtos.AuthenticateDTO;
import org.patientbot.patienttelegrambot.dtos.ChatStateDTO;
import org.patientbot.patienttelegrambot.entity.ChatState;
import org.patientbot.patienttelegrambot.entity.ChatStates;
import org.patientbot.patienttelegrambot.entity.TelegramBotResponses;
import org.patientbot.patienttelegrambot.services.AuthenticationRequestService;
import org.patientbot.patienttelegrambot.services.ChatStateRequestService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommandHandlerTest {

    @Mock
    private AuthenticationCommandHandler mockAuthenticationCommandHandler;
    @Mock
    private DoctorCommandHandler mockDoctorCommandHandler;
    @Mock
    private EmergencyCommandHandler mockEmergencyCommandHandler;

    @Mock
    private ChatStateRequestService stateRequestService;

    @Mock
    private AuthenticationRequestService authenticationRequestService;

    private CommandHandler commandHandlerUnderTest;

    private Update update;

    private ChatState chatState;

    @BeforeEach
    void setUp() {
        update = new Update();
        commandHandlerUnderTest = new CommandHandler(mockAuthenticationCommandHandler, mockDoctorCommandHandler,
                mockEmergencyCommandHandler, stateRequestService, authenticationRequestService);

        chatState = new ChatState(1L);
    }

    @Test
    void testSendResponseCommandNotFound() {
        Message message = new Message();
        message.setChat(new Chat(1L, "Long"));
        message.setText("some command that not exists");
        update.setMessage(message);

        AuthenticateDTO authenticateDTO = new AuthenticateDTO(1L, "some-token");


        when(stateRequestService.getChatState(anyLong())).thenReturn(Optional.of(chatState));
        when(authenticationRequestService.getAuthenticationStatus(anyLong())).thenReturn(Optional.of(authenticateDTO));

        SendMessage result = commandHandlerUnderTest.sendResponse(update);

        assertEquals(result.getText(), TelegramBotResponses.SOME_ERROR.getDescription());
    }


    @Test
    void testSendResponsePermissionDenied() {
        Message message = new Message();
        message.setChat(new Chat(1L, "Long"));
        message.setText("some command that not exists");
        update.setMessage(message);

        when(stateRequestService.getChatState(anyLong())).thenReturn(Optional.of(chatState));
        when(authenticationRequestService.getAuthenticationStatus(anyLong())).thenReturn(Optional.empty());

        SendMessage result = commandHandlerUnderTest.sendResponse(update);

        assertEquals(result.getText(), TelegramBotResponses.PERMISSION_DENIED_BECAUSE_OF_AUTHENTICATION.getDescription());
    }

}
