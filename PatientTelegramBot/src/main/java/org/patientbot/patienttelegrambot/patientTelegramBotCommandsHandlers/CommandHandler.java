package org.patientbot.patienttelegrambot.patientTelegramBotCommandsHandlers;

import org.patientbot.patienttelegrambot.dtos.ChatStateDTO;
import org.patientbot.patienttelegrambot.entity.ChatStates;
import org.patientbot.patienttelegrambot.exception.RestTemplateExceptionHandler;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class CommandHandler implements Command {

    private Map<String, Command> commandMap = new HashMap<>();

    private final RestTemplate restTemplate = new RestTemplateBuilder().errorHandler(new RestTemplateExceptionHandler()).build();

    private Long CHAT_ID;

    private String message;

    private String callback;

    private ChatStates chatState;


    public CommandHandler(AuthenticationCommandHandler authenticationCommandHandler) {
        commandMap.put("/authenticate", authenticationCommandHandler);
    }


    @Override
    public SendMessage sendResponse(Update update) {
        if (update.hasCallbackQuery()) {
            CHAT_ID = update.getCallbackQuery().getMessage().getChatId();
        } else if (update.hasMessage()) {
            CHAT_ID = update.getMessage().getChatId();
            message = update.getMessage().getText();
        }

        Optional<ChatStateDTO> optionalChatState = getChatState(CHAT_ID);
        if (optionalChatState.isPresent()) {
            chatState = ChatStates.valueOf(optionalChatState.get().getChatStates());
        } else {
            Optional<ChatStateDTO> newChatState = createChatState(CHAT_ID);
            if (newChatState.isPresent()) {
                chatState = ChatStates.valueOf(newChatState.get().getChatStates());
            } else {
                System.out.println("smth wrong in handler");
            }
        }

        Command command = commandMap.get(message);
        if (command != null) {
            return command.sendResponse(update);
        } else if (command == null && !chatState.equals(ChatStates.DEFAULT)) {
            command = commandMap.get(chatState.getDescription());
            return command.sendResponse(update);
        } else {
            return new SendMessage(String.valueOf(CHAT_ID), "error somewhere");
        }

    }


    private Optional<ChatStateDTO> getChatState(Long chatID) {
        ResponseEntity<ChatStateDTO> optionalChatState = restTemplate.getForEntity("http://localhost:8082/chat/api/chatstate/%s".formatted(chatID), ChatStateDTO.class);
        System.out.println(optionalChatState.getStatusCode());
        return optionalChatState.getStatusCode().is2xxSuccessful() ?
                Optional.of(optionalChatState.getBody()) :
                Optional.empty();
    }

    private Optional<ChatStateDTO> createChatState(Long chatID) {
        System.out.println("http://localhost:8082/chat/api/chatstate?chatID=%s".formatted(chatID));
        ResponseEntity<ChatStateDTO> optionalChatState = restTemplate.postForEntity("http://localhost:8082/chat/api/chatstate?chatID=%s".formatted(chatID), null, ChatStateDTO.class);
        return optionalChatState.getStatusCode().is2xxSuccessful() ?
                Optional.of(optionalChatState.getBody()) :
                Optional.empty();
    }
}
