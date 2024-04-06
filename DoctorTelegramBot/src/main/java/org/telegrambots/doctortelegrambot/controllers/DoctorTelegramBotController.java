package org.telegrambots.doctortelegrambot.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegrambots.doctortelegrambot.configurations.DoctorTelegramBotConfiguration;
import org.telegrambots.doctortelegrambot.doctorCommandsHandlers.CommandHandler;

@Component
@RequiredArgsConstructor
public class DoctorTelegramBotController extends TelegramLongPollingBot {

    private final DoctorTelegramBotConfiguration doctorTelegramBotConfiguration;

    private final CommandHandler commandHandler;

    @Override
    public String getBotToken() {
        return doctorTelegramBotConfiguration.getToken();
    }

    @Override
    public String getBotUsername() {
        return doctorTelegramBotConfiguration.getBotName();
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            execute(commandHandler.handleCommands(update));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


    public void sendMessageToDoctor() throws TelegramApiException {
        Message execute = execute(new SendMessage());
    }
}
