package org.telegrambots.patientBot;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.polls.Poll;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@PropertySource("classpath:application.properties")
@RequiredArgsConstructor
public class PatientBotService extends TelegramLongPollingBot {

    private final BotConfiguration botConfiguration;

    private final TelegramBotIncomingMessageValidator validator;

    private final SendMessage sendMessage = new SendMessage();


    @Override
    public String getBotUsername() {
        return botConfiguration.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfiguration.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        String incomingMessage = update.getMessage().getText();
        if (validator.validate(incomingMessage)) {
            sendMessage.setText("validation success");
        } else {
            sendMessage.setText("validation failed");
        }
        sendMessage.setChatId(update.getMessage().getChatId());
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


}
