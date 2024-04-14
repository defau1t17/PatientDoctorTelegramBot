package org.telegrambots.doctortelegrambot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegrambots.doctortelegrambot.dto.AuthenticatedUserDTO;
import org.telegrambots.doctortelegrambot.entities.ChatState;
import org.telegrambots.doctortelegrambot.entities.ChatStates;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@PropertySource(value = "classpath:application.properties")
public class ChatStateRequestService {

    @Value(value = "${hospital.service.address}")
    private String hospitalService;
    @Value(value = "${chat.service.address}")
    private String chatService;
    private final RestTemplate restTemplate;

    public Optional<ChatState> getChatState(long chatID) {
        ResponseEntity<ChatState> optionalChatState = restTemplate.getForEntity("http://%s:8082/chat/api/chatstate/%s"
                .formatted(chatService, chatID), ChatState.class);
        return optionalChatState.getStatusCode().is2xxSuccessful() ?
                Optional.of(optionalChatState.getBody()) :
                Optional.empty();
    }

    public Optional<ChatState> updateChatState(long chatID, ChatStates chatStates) {
        ResponseEntity<ChatState> optionalUpdateChatState = restTemplate.postForEntity("http://%s:8082/chat/api/chatstate/%s/update?state=%s"
                .formatted(chatStates, chatID, chatStates.toString()), null, ChatState.class);
        return optionalUpdateChatState.getStatusCode().is2xxSuccessful() ?
                Optional.of(optionalUpdateChatState.getBody()) :
                Optional.empty();
    }

    public Optional<ChatState> createChatState(long chatID) {
        ResponseEntity<ChatState> optionalChatState = restTemplate.postForEntity("http://%s:8082/chat/api/chatstate?chatID=%s"
                .formatted(chatService, chatID), null, ChatState.class);
        return optionalChatState.getStatusCode().is2xxSuccessful() ?
                Optional.of(optionalChatState.getBody()) :
                Optional.empty();
    }


    public Optional<ChatState> moveChatStateToNextState(Long chatID) {
        ResponseEntity<ChatState> optionalUpdatedState = restTemplate.postForEntity("http://%s:8082/chat/api/chatstate/%s/move?move=next"
                .formatted(chatService, chatID), null, ChatState.class);
        return optionalUpdatedState.getStatusCode().is2xxSuccessful() ?
                Optional.of(optionalUpdatedState.getBody()) :
                Optional.empty();
    }

    public Optional<AuthenticatedUserDTO> updateChatIDInHospitalDatabase(long chatID, String token) {
        ResponseEntity<AuthenticatedUserDTO> optionalUpdate = restTemplate.exchange("http://%s:8084/hospital/api/patients?chatID=%s&token=%s"
                .formatted(hospitalService, chatID, token), HttpMethod.PATCH, null, AuthenticatedUserDTO.class);
        return optionalUpdate.getStatusCode().is2xxSuccessful() ?
                Optional.of(optionalUpdate.getBody()) :
                Optional.empty();
    }

    public Optional<ChatState> rollBackChatStateToDefault(long chatID) {
        ResponseEntity<ChatState> optionalUpdateChatState = restTemplate.postForEntity("http://%s:8082/chat/api/chatstate/%s/update?state=%s"
                .formatted(chatService, chatID, ChatStates.DEFAULT.name()), null, ChatState.class);
        return optionalUpdateChatState.getStatusCode().is2xxSuccessful() ?
                Optional.of(optionalUpdateChatState.getBody()) :
                Optional.empty();
    }

}
