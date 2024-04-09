package org.patientbot.patienttelegrambot.patientTelegramBotCommandsHandlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.patientbot.patienttelegrambot.entity.ChatState;
import org.patientbot.patienttelegrambot.entity.TelegramBotResponses;
import org.patientbot.patienttelegrambot.services.ChatStateRequestService;
import org.patientbot.patienttelegrambot.stateResolvers.DoctorStateResolver;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoctorCommandHandlerTest {

    @Mock
    private DoctorStateResolver mockStateResolver;
    @Mock
    private ChatStateRequestService mockChatStateRequestService;

    private DoctorCommandHandler doctorCommandHandlerUnderTest;

    private Update update;

    private Message message;

    private ChatState chatState;

    @BeforeEach
    void setUp() {
        message = new Message();
        update = new Update();
        chatState = new ChatState(1L);
        doctorCommandHandlerUnderTest = new DoctorCommandHandler(mockStateResolver, mockChatStateRequestService);
    }

    @Test
    void testSendResponseSuccess() {
        message.setText("test");
        message.setChat(new Chat(1L, " test "));
        update.setMessage(message);

        when(mockChatStateRequestService.getChatState(anyLong())).thenReturn(Optional.of(chatState));

        when(mockStateResolver.responseOnState(any(), any())).thenReturn(new SendMessage("1", "Hello World"));

        final SendMessage result = doctorCommandHandlerUnderTest.sendResponse(update);

        assertNotEquals(result, null);
        assertFalse(result.getText().isEmpty());
        assertNotEquals(result.getText(), TelegramBotResponses.SOME_ERROR);
        assertNotEquals(result.getText(), TelegramBotResponses.SYNTAX_ERROR);
    }

    @Test
    void testSendResponseFailure() {
        message.setText("test");
        message.setChat(new Chat(1L, " test "));
        update.setMessage(message);

        when(mockChatStateRequestService.getChatState(anyLong())).thenReturn(Optional.empty());

        final SendMessage result = doctorCommandHandlerUnderTest.sendResponse(update);

        assertNotEquals(result, null);
        assertFalse(result.getText().isEmpty());
        assertEquals(result.getText(), TelegramBotResponses.SOME_ERROR.getDescription());
    }
}
