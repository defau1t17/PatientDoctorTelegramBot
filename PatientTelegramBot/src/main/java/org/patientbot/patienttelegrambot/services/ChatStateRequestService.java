package org.patientbot.patienttelegrambot.services;

import lombok.RequiredArgsConstructor;
import org.patientbot.patienttelegrambot.dtos.AuthenticatedUserDTO;
import org.patientbot.patienttelegrambot.dtos.ChatStateDTO;
import org.patientbot.patienttelegrambot.entity.ChatState;
import org.patientbot.patienttelegrambot.entity.ChatStates;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@PropertySource(value = "classpath:application.properties")
public class ChatStateRequestService {

    @Value(value = "${chat.service.address}")
    private String serviceAddress;
    @Value(value = "${hospital.service.address}")
    private String hospitalAddress;
    private final RestTemplate restTemplate;

    public Optional<ChatState> getChatState(long chatID) {
        ResponseEntity<ChatState> optionalChatState = restTemplate.getForEntity("http://%s:8082/chat/api/chatstate/%s".formatted(serviceAddress, chatID), ChatState.class);
        return optionalChatState.getStatusCode().is2xxSuccessful() ?
                Optional.of(optionalChatState.getBody()) :
                Optional.empty();
    }

    public Optional<ChatState> createChatState(long chatID) {
        ResponseEntity<ChatState> optionalChatState = restTemplate.postForEntity("http://%s:8082/chat/api/chatstate?chatID=%s".formatted(serviceAddress, chatID), null, ChatState.class);
        return optionalChatState.getStatusCode().is2xxSuccessful() ?
                Optional.of(optionalChatState.getBody()) :
                Optional.empty();
    }

    public Optional<ChatState> moveChatStateToNextState(Long chatID) {
        ResponseEntity<ChatState> optionalUpdatedState = restTemplate.postForEntity("http://%s:8082/chat/api/chatstate/%s/move?move=next"
                .formatted(serviceAddress, chatID), null, ChatState.class);
        return optionalUpdatedState.getStatusCode().is2xxSuccessful() ?
                Optional.of(optionalUpdatedState.getBody()) :
                Optional.empty();
    }

    public Optional<ChatState> updateChatState(Long chatID, ChatStates chatStates) {
        ResponseEntity<ChatState> optionalChatStateUpdate = restTemplate.postForEntity("http://%s:8082/chat/api/chatstate/%s/update?state=%s"
                .formatted(serviceAddress, chatID, chatStates.toString()), null, ChatState.class);
        return optionalChatStateUpdate.getStatusCode().is2xxSuccessful() ?
                Optional.of(optionalChatStateUpdate.getBody()) :
                Optional.empty();
    }

    public Optional<AuthenticatedUserDTO> updateChatIDInHospitalDatabase(long chatID, String token) {
        ResponseEntity<AuthenticatedUserDTO> optionalUpdate = restTemplate.exchange("http://%s:8084/hospital/api/patients?chatID=%s&token=%s"
                .formatted(hospitalAddress,chatID, token), HttpMethod.PATCH, null, AuthenticatedUserDTO.class);
        return optionalUpdate.getStatusCode().is2xxSuccessful() ?
                Optional.of(optionalUpdate.getBody()) :
                Optional.empty();
    }

}
