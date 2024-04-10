package org.telegrambots.doctortelegrambot.stateResolvers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegrambots.doctortelegrambot.dto.AuthenticateDTO;
import org.telegrambots.doctortelegrambot.dto.AuthenticatedUserDTO;
import org.telegrambots.doctortelegrambot.entities.ChatState;
import org.telegrambots.doctortelegrambot.entities.ChatStates;
import org.telegrambots.doctortelegrambot.entities.TelegramBotResponses;
import org.telegrambots.doctortelegrambot.services.AuthenticationRequestService;
import org.telegrambots.doctortelegrambot.services.ChatStateRequestService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationStateResolverTest {

    @Mock
    private AuthenticationRequestService mockRequestService;
    @Mock
    private ChatStateRequestService mockChatStateRequestService;

    private AuthenticationStateResolver authenticationStateResolverUnderTest;

    private Update update;

    private Message message;

    private ChatState chatState;

    @BeforeEach
    void setUp() {
        update = new Update();
        message = new Message();
        chatState = new ChatState(1L);

        authenticationStateResolverUnderTest = new AuthenticationStateResolver(mockRequestService,
                mockChatStateRequestService);
    }

    @Test
    void testResponseOnStateWhenStateIsDefault() {
        message.setText("");
        message.setChat(new Chat(1L, "Test"));
        update.setMessage(message);

        when(mockChatStateRequestService.updateChatState(anyLong(), any(ChatStates.class)))
                .thenReturn(Optional.of(chatState));

        final SendMessage result = authenticationStateResolverUnderTest.responseOnState(chatState, update);

        assertNotEquals(result, null);
        assertEquals(result.getText(), "Please, write your personal access token \n(Avoid '@,|,/,!,#,$,%,^,&,*,(,),{,},[,]' symbols)\n");
    }

    @Test
    void testResponseOnStateWhenStateIsDefaultErrorHappened() {
        message.setText("");
        message.setChat(new Chat(1L, "Test"));
        update.setMessage(message);

        when(mockChatStateRequestService.updateChatState(anyLong(), any(ChatStates.class)))
                .thenReturn(Optional.empty());

        final SendMessage result = authenticationStateResolverUnderTest.responseOnState(chatState, update);

        assertNotEquals(result, null);
        assertEquals(result.getText(), TelegramBotResponses.SOME_ERROR.getDescription());
    }

    @Test
    void testResponseOnStateWhenStateIsWaitingForTokenSuccess() {
        message.setText("some-token");
        message.setChat(new Chat(1L, "Test"));
        update.setMessage(message);

        chatState.setChatStates(ChatStates.WAITING_FOR_TOKEN);

        AuthenticateDTO authenticateDTO = spy(AuthenticateDTO.class);
        AuthenticatedUserDTO authenticatedUserDTO = new AuthenticatedUserDTO("test", "test");
        when(mockRequestService.authenticate(anyLong(), anyString())).thenReturn(Optional.of(authenticateDTO));
        when(mockChatStateRequestService.updateChatIDInHospitalDatabase(anyLong(), anyString())).thenReturn(Optional.of(authenticatedUserDTO));

        final SendMessage result = authenticationStateResolverUnderTest.responseOnState(chatState, update);

        assertNotEquals(result, null);
        assertEquals(result.getText(), "%s\nWelcome %s %s"
                .formatted(TelegramBotResponses.AUTH_PASSED.getDescription(), authenticatedUserDTO.getName(), authenticatedUserDTO.getSecondName()));
        verify(mockChatStateRequestService).moveChatStateToNextState(anyLong());
    }


    @Test
    void testResponseOnStateWhenStateIsWaitingForTokenBadCredentials() {
        message.setText("some-token");
        message.setChat(new Chat(1L, "Test"));
        update.setMessage(message);

        chatState.setChatStates(ChatStates.WAITING_FOR_TOKEN);


        when(mockRequestService.authenticate(anyLong(), anyString())).thenReturn(Optional.empty());

        final SendMessage result = authenticationStateResolverUnderTest.responseOnState(chatState, update);

        assertNotEquals(result, null);
        assertEquals(result.getText(), TelegramBotResponses.BAD_CREDS.getDescription());
    }

}
