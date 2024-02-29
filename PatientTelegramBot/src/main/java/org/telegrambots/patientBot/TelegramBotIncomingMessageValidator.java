package org.telegrambots.patientBot;

import org.springframework.stereotype.Component;
import org.telegrambots.additionals.TelegramBotPatientCommands;

import java.util.Arrays;

@Component
public class TelegramBotIncomingMessageValidator {
    public boolean validate(String message) {
        if (!message.startsWith("/")) return false;
        if (Arrays.stream(TelegramBotPatientCommands
                        .values())
                        .noneMatch(telegramBotPatientCommands ->
                        telegramBotPatientCommands.name()
                                .equals(message.substring(1))))
            return false;
        return true;
    }
}
