package org.telegrambots.doctortelegrambot.doctorCommandsHandlers;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegrambots.doctortelegrambot.exceptions.RestTemplateExceptionHandler;

@Component
public class ShiftCommandHandler implements Command {
    private final SendMessage sendMessage = new SendMessage();


    private final RestTemplate restTemplate = new RestTemplateBuilder().errorHandler(new RestTemplateExceptionHandler()).build();

    @Override
    public SendMessage sendResponse(Update update) {
        return null;
    }
}
