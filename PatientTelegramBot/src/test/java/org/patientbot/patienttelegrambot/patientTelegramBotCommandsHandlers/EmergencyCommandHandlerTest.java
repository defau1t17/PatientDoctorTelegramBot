package org.patientbot.patienttelegrambot.patientTelegramBotCommandsHandlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.patientbot.patienttelegrambot.services.EmergencyRequestService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmergencyCommandHandlerTest {

    @Mock
    private EmergencyRequestService mockRequestService;

    private EmergencyCommandHandler emergencyCommandHandlerUnderTest;

    private Update update;

    @BeforeEach
    void setUp() {
        update = new Update();
        emergencyCommandHandlerUnderTest = new EmergencyCommandHandler(mockRequestService);
    }

    @Test
    void testSendResponseSuccess() {
        final Message message = new Message();
        message.setText("/emergency");
        message.setChat(new Chat(1L, "Long"));

        when(mockRequestService.sendEmergencyHelpRequest(anyLong())).thenReturn(true);
        final SendMessage result = emergencyCommandHandlerUnderTest.sendResponse(update);
        assertEquals(result.getText(), "Your request has been successfully delivered to the doctors. Stand by, someone will be with you shortly");
    }

    @Test
    void testSendResponseFailure() {
        final Message message = new Message();
        message.setText("/emergency");
        message.setChat(new Chat(1L, "Long"));

        when(mockRequestService.sendEmergencyHelpRequest(anyLong())).thenReturn(false);
        final SendMessage result = emergencyCommandHandlerUnderTest.sendResponse(update);
        assertEquals(result.getText(), "Your request was not successfully delivered");
    }
}
