package org.patientbot.patienttelegrambot.patientTelegramBotCommandsHandlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CommandHandlerTest {

    @Mock
    private AuthenticationCommandHandler mockAuthenticationCommandHandler;

    private CommandHandler commandHandlerUnderTest;

    @BeforeEach
    void setUp() {
        commandHandlerUnderTest = new CommandHandler(mockAuthenticationCommandHandler);
    }

    @Test
    void testSendResponse() {
        // Setup
        final Update update = new Update();
        final Message message = new Message();
        message.setText("text");
        update.setMessage(message);
        final CallbackQuery callbackQuery = new CallbackQuery();
        final Message message1 = new Message();
        message1.setText("text");
        callbackQuery.setMessage(message1);
        update.setCallbackQuery(callbackQuery);

        final SendMessage expectedResult = SendMessage.builder().build();

        // Run the test
        final SendMessage result = commandHandlerUnderTest.sendResponse(update);

        // Verify the results
        assertEquals(expectedResult, result);
    }
}
