package org.telegrambots.doctortelegrambot.doctorCommandsHandlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegrambots.doctortelegrambot.entities.ChatState;
import org.telegrambots.doctortelegrambot.entities.TelegramBotResponses;
import org.telegrambots.doctortelegrambot.services.ChatStateRequestService;
import org.telegrambots.doctortelegrambot.stateResolvers.AuthenticationStateResolver;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationCommandHandlerTest {

    @Mock
    private ChatStateRequestService mockChatStateRequestService;
    @Mock
    private AuthenticationStateResolver mockStateResolver;

    private AuthenticationCommandHandler authenticationCommandHandlerUnderTest;

    private Update update;

    @BeforeEach
    void setUp() {
        update = new Update();
        authenticationCommandHandlerUnderTest = new AuthenticationCommandHandler(mockChatStateRequestService,
                mockStateResolver);
    }

    @Test
    void testSendResponse() {
        Message message = new Message();
        message.setText("some-text");
        message.setChat(new Chat(1L, "test"));

        update.setMessage(message);

        ChatState chatState = spy(ChatState.class);

        when(mockChatStateRequestService.getChatState(anyLong())).thenReturn(Optional.of(chatState));

        when(mockStateResolver.responseOnState(chatState, update)).thenReturn(spy(SendMessage.class));

        final SendMessage result = authenticationCommandHandlerUnderTest.sendResponse(update);

        assertNotEquals(result, null);
    }

    @Test
    void testSendResponse_ChatStateRequestServiceReturnsAbsent() {

        when(mockChatStateRequestService.getChatState(anyLong())).thenReturn(Optional.empty());

        final SendMessage result = authenticationCommandHandlerUnderTest.sendResponse(update);

        assertNotEquals(result, null);

        assertEquals(result.getText(), TelegramBotResponses.SOME_ERROR.getDescription());
    }
}
