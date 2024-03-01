package org.telegrambots.doctortelegrambot.doctorBot;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class BotController extends TelegramLongPollingBot {

    private final BotConfiguration botConfiguration;

    @Override
    public String getBotToken() {
        return botConfiguration.getToken();
    }

    @Override
    public String getBotUsername() {
        return botConfiguration.getBotName();
    }

    @Override
    public void onUpdateReceived(Update update) {

    }


}
