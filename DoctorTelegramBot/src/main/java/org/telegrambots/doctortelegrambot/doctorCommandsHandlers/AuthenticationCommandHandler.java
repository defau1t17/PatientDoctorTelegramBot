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
import org.telegrambots.doctortelegrambot.entities.ChatState;
import org.telegrambots.doctortelegrambot.entities.ChatStates;
import org.telegrambots.doctortelegrambot.entities.TelegramBotResponses;
import org.telegrambots.doctortelegrambot.exceptions.RestTemplateExceptionHandler;
import org.telegrambots.doctortelegrambot.repositories.ChatStateRepository;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthenticationCommandHandler implements Command, StateUpdatable {

    private final SendMessage sendMessage = new SendMessage();

    private final ChatStateRepository chatStateRepository;
    private final RestTemplate restTemplate = new RestTemplateBuilder().errorHandler(new RestTemplateExceptionHandler()).build();

    private String responseMessage = "";
    private final String moveReference = "/authenticate";


    @Override
    public SendMessage sendResponse(Update update) {
        ChatState chatState = chatStateRepository.findChatStateByChatID(Math.toIntExact(update.getMessage().getChatId())).orElse(null);
        responseOnState(chatState, update);
        sendMessage.setText(responseMessage);
        sendMessage.setChatId(update.getMessage().getChatId());
        StateUpdatable.updateState(chatStateRepository, chatState, moveReference);
        return sendMessage;
    }

    @Override
    public void responseOnState(ChatState chatState, Update update) {
        String text = update.getMessage().getText();
        if (chatState == null) {
            chatState = chatStateRepository.save(new ChatState(Math.toIntExact(update.getMessage().getChatId())));
        }
        switch (chatState.getChatStates()) {
            case DEFAULT -> {
                this.responseMessage = "Please, write your personal access token \n(Avoid '@,|,/,!,#,$,%,^,&,*,(,),{,},[,]' symbols)\n";
            }
            case WAITING_FOR_TOKEN -> {
                if (text != null && validateToken(text)) {
                    if (authenticateClient(Math.toIntExact(update.getMessage().getChatId()), text)) {
                        this.responseMessage = TelegramBotResponses.AUTH_PASSED.getDescription();
                    } else {
                        this.responseMessage = TelegramBotResponses.BAD_CREDS.getDescription();
                    }
                } else {
                    this.responseMessage = TelegramBotResponses.SYNTAX_ERROR.getDescription();
                }
            }
            default -> {
                chatState.setChatStates(ChatStates.DEFAULT);
                chatState = chatStateRepository.save(chatState);
                responseOnState(chatState, update);
            }
        }
    }

    private boolean authenticateClient(int chatID, String token) {
        Map<String, String> values = Map.of("chatID", String.valueOf(chatID), "token", token);
        ResponseEntity<HttpStatus> forEntity = restTemplate.exchange("http://localhost:8080/api/v1/authenticate?chatID={chatID}&token={token}", HttpMethod.POST, null, HttpStatus.class, values);
        return forEntity.getStatusCode().equals(HttpStatus.OK);
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
