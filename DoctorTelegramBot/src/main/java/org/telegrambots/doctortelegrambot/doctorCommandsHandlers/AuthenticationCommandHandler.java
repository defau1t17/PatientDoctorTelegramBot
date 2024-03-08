package org.telegrambots.doctortelegrambot.doctorCommandsHandlers;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegrambots.doctortelegrambot.entities.TelegramBotResponses;
import org.telegrambots.doctortelegrambot.exceptions.RestTemplateExceptionHandler;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthenticationCommandHandler implements Command {

    private final SendMessage sendMessage = new SendMessage();

    private final RestTemplate restTemplate = new RestTemplateBuilder().errorHandler(new RestTemplateExceptionHandler()).build();

    @Override
    public SendMessage sendResponse(Update update) {
        String extractedToken = extractTokenFromMessage(update.getMessage().getText());
        if (extractedToken != null && validateToken(extractedToken)) {
            if (authenticateClient(Math.toIntExact(update.getMessage().getChatId()), extractedToken)) {
                sendMessage.setText(TelegramBotResponses.AUTH_PASSED.getDescription());
            } else {
                sendMessage.setText(TelegramBotResponses.BAD_CREDS.getDescription());
            }
        } else {
            sendMessage.setText(TelegramBotResponses.SYNTAX_ERROR.getDescription());
        }
        sendMessage.setChatId(update.getMessage().getChatId());
        return sendMessage;
    }

    private boolean authenticateClient(int chatID, String token) {
        Map<String, String> values = Map.of("chatID", String.valueOf(chatID), "token", token);
        ResponseEntity<HttpStatus> forEntity = restTemplate.exchange("http://localhost:8080/api/v1/authenticate?chatID={chatID}&token={token}", HttpMethod.POST, null, HttpStatus.class, values);
        return forEntity.getStatusCode().equals(HttpStatus.OK);
    }

    private String extractTokenFromMessage(String message) {
        var messageArray = message.split(" ");
        return messageArray.length == 2 ?
                messageArray[1] :
                null;
    }

    private boolean validateToken(String token) {
        try {
            UUID.fromString(token);
        } catch (IllegalArgumentException exception) {
            return false;
        }
        return true;
    }

}
