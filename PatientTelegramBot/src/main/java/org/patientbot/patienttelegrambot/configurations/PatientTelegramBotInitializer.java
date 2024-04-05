package org.patientbot.patienttelegrambot.configurations;

import lombok.RequiredArgsConstructor;
import org.patientbot.patienttelegrambot.controllers.PatientTelegramBotController;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
@RequiredArgsConstructor
public class PatientTelegramBotInitializer {

    private final PatientTelegramBotController patientTelegramBotController;

    @EventListener({ContextRefreshedEvent.class})
    public void initBot() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(patientTelegramBotController);
    }

}
