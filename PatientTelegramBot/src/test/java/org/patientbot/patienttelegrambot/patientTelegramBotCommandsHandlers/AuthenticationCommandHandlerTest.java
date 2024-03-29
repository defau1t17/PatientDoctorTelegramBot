package org.patientbot.patienttelegrambot.patientTelegramBotCommandsHandlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.patientbot.patienttelegrambot.entity.ChatStates;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthenticationCommandHandlerTest {

    private AuthenticationCommandHandler authenticationCommandHandlerUnderTest;

    @BeforeEach
    void setUp() {
        authenticationCommandHandlerUnderTest = new AuthenticationCommandHandler();
    }

    @Test
    void testSendResponse() {
        // Setup
        final Update update = new Update();
        update.setUpdateId(0);
        final Message message = new Message();
        message.setMessageId(0);
        message.setMessageThreadId(0);
        message.setText("text");
        update.setMessage(message);

        final SendMessage expectedResult = SendMessage.builder()
                .chatId("chatId")
                .text("text")
                .build();

        // Run the test
        final SendMessage result = authenticationCommandHandlerUnderTest.sendResponse(update);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testResponseOnState() {
        // Setup
        final Update update = new Update();
        update.setUpdateId(0);
        final Message message = new Message();
        message.setMessageId(0);
        message.setMessageThreadId(0);
        message.setText("text");
        update.setMessage(message);

        // Run the test
        authenticationCommandHandlerUnderTest.responseOnState(ChatStates.DEFAULT, update);

        // Verify the results
    }
}
