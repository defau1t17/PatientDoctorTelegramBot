package org.telegrambots;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegrambots.patientBot.BotConfiguration;
import org.telegrambots.patientBot.PatientBotService;

@SpringBootApplication
public class PatientDoctorTelegramBotApplication {

    public static void main(String[] args) throws TelegramApiException {
        SpringApplication.run(PatientDoctorTelegramBotApplication.class, args);
    }

}
