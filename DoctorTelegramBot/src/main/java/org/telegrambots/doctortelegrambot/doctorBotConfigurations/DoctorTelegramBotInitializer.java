package org.telegrambots.doctortelegrambot.doctorBotConfigurations;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.telegrambots.doctortelegrambot.contollers.DoctorTelegramBotController;

@Component
@RequiredArgsConstructor
public class DoctorTelegramBotInitializer {

    private final DoctorTelegramBotController doctorTelegramBotController;

    @EventListener({ContextRefreshedEvent.class})
    public void initBot() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(doctorTelegramBotController);

    }

}
