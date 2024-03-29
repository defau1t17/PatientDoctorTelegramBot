package org.patientbot.patienttelegrambot.patientTelegramBotCommandsHandlers;

import org.patientbot.patienttelegrambot.dtos.AuthenticateDTO;
import org.patientbot.patienttelegrambot.dtos.AuthenticatedUserDTO;
import org.patientbot.patienttelegrambot.dtos.ChatStateDTO;
import org.patientbot.patienttelegrambot.entity.ChatStates;
import org.patientbot.patienttelegrambot.exception.RestTemplateExceptionHandler;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.patientbot.patienttelegrambot.entity.TelegramBotResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@Component
public class AuthenticationCommandHandler implements Command, State {

    private final RestTemplate restTemplate = new RestTemplateBuilder()
            .errorHandler(new RestTemplateExceptionHandler())
            .build();

    private final SendMessage sendMessage = new SendMessage();

    private String responseMessage = "test";

    private long CHAT_ID = 0;

    @Override
    public SendMessage sendResponse(Update update) {
        Optional<ChatStateDTO> optionalChatStates = getChatState(update.getMessage().getChatId());
        if (optionalChatStates.isPresent()) {
            responseOnState(ChatStates.valueOf(optionalChatStates.get().getChatStates()), update);
        } else {
            responseMessage = "smth went wrong";
        }
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText(responseMessage);
        return sendMessage;
    }

    @Override
    public void responseOnState(ChatStates chatState, Update update) {
        switch (chatState) {
            case DEFAULT -> {
                if (updateChatState(update.getMessage().getChatId(), ChatStates.WAITING_FOR_TOKEN).isPresent()) {
                    this.responseMessage = "Please, write your personal access token \n(Avoid '@,|,/,!,#,$,%,^,&,*,(,),{,},[,]' symbols)\n";
                } else {
                    responseMessage = "Some error happened...";
                }
            }
            case WAITING_FOR_TOKEN -> {
                CHAT_ID = update.getMessage().getChatId();
                String token = update.getMessage().getText();
                Optional<AuthenticatedUserDTO> updateDTO = updateChatIDInHospitalDatabase(CHAT_ID, token);
                Optional<AuthenticateDTO> optionalAuthenticate = authenticate(CHAT_ID, token);
                if (optionalAuthenticate.isPresent() && updateDTO.isPresent()) {
                    moveChatStateToNextState(CHAT_ID);
                    responseMessage = "%s\nWelcome %s %s".formatted(TelegramBotResponses.AUTH_PASSED, updateDTO.get().getName(), updateDTO.get().getSecondName());
                } else if (optionalAuthenticate.isEmpty()) {
                    responseMessage = "";
                }
            }
        }
    }

    private Optional<ChatStateDTO> getChatState(Long chatID) {
        ResponseEntity<ChatStateDTO> optionalChatState = restTemplate.getForEntity("http://localhost:8082/chat/api/chatstate/%s"
                .formatted(chatID), ChatStateDTO.class);
        return optionalChatState.getStatusCode().is2xxSuccessful() ?
                Optional.of(optionalChatState.getBody()) :
                Optional.empty();
    }

    private Optional<ChatStateDTO> moveChatStateToNextState(Long chatID) {
        ResponseEntity<ChatStateDTO> optionalUpdatedState = restTemplate.postForEntity("http://localhost:8082/chat/api/chatstate/%s/move?move=next"
                .formatted(chatID), null, ChatStateDTO.class);
        return optionalUpdatedState.getStatusCode().is2xxSuccessful() ?
                Optional.of(optionalUpdatedState.getBody()) :
                Optional.empty();
    }

    private Optional<ChatStateDTO> updateChatState(Long chatID, ChatStates chatStates) {
        ResponseEntity<ChatStateDTO> optionalChatStateUpdate = restTemplate.postForEntity("http://localhost:8082/chat/api/chatstate/%s/update?state=%s"
                .formatted(chatID, chatStates.toString()), null, ChatStateDTO.class);
        return optionalChatStateUpdate.getStatusCode().is2xxSuccessful() ?
                Optional.of(optionalChatStateUpdate.getBody()) :
                Optional.empty();
    }

    private Optional<AuthenticateDTO> authenticate(Long chatID, String token) {
        ResponseEntity<AuthenticateDTO> optionalAuthentication = restTemplate.exchange("http://localhost:8082/chat/api/authenticate?chatID=%s&token=%s"
                .formatted(chatID, token), HttpMethod.PATCH, null, AuthenticateDTO.class);
        return optionalAuthentication.getStatusCode().is2xxSuccessful() ?
                Optional.of(optionalAuthentication.getBody()) :
                Optional.empty();
    }


    private Optional<AuthenticatedUserDTO> updateChatIDInHospitalDatabase(long chatID, String token) {
        ResponseEntity<AuthenticatedUserDTO> optionalUpdate = restTemplate.exchange("http://localhost:8084/hospital/api/patients?chatID=%s&token=%s"
                .formatted(chatID, token), HttpMethod.PATCH, null, AuthenticatedUserDTO.class);
        return optionalUpdate.getStatusCode().is2xxSuccessful() ?
                Optional.of(optionalUpdate.getBody()) :
                Optional.empty();
    }


}
