package org.patientbot.patienttelegrambot.controllers;

import lombok.RequiredArgsConstructor;
import org.patientbot.patienttelegrambot.configurations.PatientTelegramBotConfiguration;
import org.patientbot.patienttelegrambot.patientTelegramBotCommandsHandlers.CommandHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
public class PatientTelegramBotController extends TelegramLongPollingBot {

    private final PatientTelegramBotConfiguration patientTelegramBotConfiguration;

    private final CommandHandler commandHandler;

    @Override
    public String getBotToken() {
        return patientTelegramBotConfiguration.getToken();
    }

    @Override
    public String getBotUsername() {
        return patientTelegramBotConfiguration.getBotName();
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            execute(commandHandler.sendResponse(update));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
