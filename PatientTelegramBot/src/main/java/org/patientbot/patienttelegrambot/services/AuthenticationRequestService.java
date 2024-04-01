package org.patientbot.patienttelegrambot.services;

import org.patientbot.patienttelegrambot.dtos.AuthenticateDTO;
import org.patientbot.patienttelegrambot.dtos.AuthenticatedUserDTO;
import org.patientbot.patienttelegrambot.dtos.ChatStateDTO;
import org.patientbot.patienttelegrambot.entity.ChatStates;
import org.patientbot.patienttelegrambot.exception.RestTemplateExceptionHandler;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class AuthenticationRequestService {

    private final RestTemplate restTemplate = new RestTemplateBuilder()
            .errorHandler(new RestTemplateExceptionHandler())
            .build();

    public Optional<ChatStateDTO> getChatState(Long chatID) {
        ResponseEntity<ChatStateDTO> optionalChatState = restTemplate.getForEntity("http://localhost:8082/chat/api/chatstate/%s"
                .formatted(chatID), ChatStateDTO.class);
        return optionalChatState.getStatusCode().is2xxSuccessful() ?
                Optional.of(optionalChatState.getBody()) :
                Optional.empty();
    }

    public Optional<ChatStateDTO> moveChatStateToNextState(Long chatID) {
        ResponseEntity<ChatStateDTO> optionalUpdatedState = restTemplate.postForEntity("http://localhost:8082/chat/api/chatstate/%s/move?move=next"
                .formatted(chatID), null, ChatStateDTO.class);
        return optionalUpdatedState.getStatusCode().is2xxSuccessful() ?
                Optional.of(optionalUpdatedState.getBody()) :
                Optional.empty();
    }

    public Optional<ChatStateDTO> updateChatState(Long chatID, ChatStates chatStates) {
        ResponseEntity<ChatStateDTO> optionalChatStateUpdate = restTemplate.postForEntity("http://localhost:8082/chat/api/chatstate/%s/update?state=%s"
                .formatted(chatID, chatStates.toString()), null, ChatStateDTO.class);
        return optionalChatStateUpdate.getStatusCode().is2xxSuccessful() ?
                Optional.of(optionalChatStateUpdate.getBody()) :
                Optional.empty();
    }

    public Optional<AuthenticateDTO> authenticate(Long chatID, String token) {
        ResponseEntity<AuthenticateDTO> optionalAuthentication = restTemplate.exchange("http://localhost:8082/chat/api/authenticate?chatID=%s&token=%s"
                .formatted(chatID, token), HttpMethod.PATCH, null, AuthenticateDTO.class);
        return optionalAuthentication.getStatusCode().is2xxSuccessful() ?
                Optional.of(optionalAuthentication.getBody()) :
                Optional.empty();
    }


    public Optional<AuthenticatedUserDTO> updateChatIDInHospitalDatabase(long chatID, String token) {
        ResponseEntity<AuthenticatedUserDTO> optionalUpdate = restTemplate.exchange("http://localhost:8084/hospital/api/patients?chatID=%s&token=%s"
                .formatted(chatID, token), HttpMethod.PATCH, null, AuthenticatedUserDTO.class);
        return optionalUpdate.getStatusCode().is2xxSuccessful() ?
                Optional.of(optionalUpdate.getBody()) :
                Optional.empty();
    }


}
