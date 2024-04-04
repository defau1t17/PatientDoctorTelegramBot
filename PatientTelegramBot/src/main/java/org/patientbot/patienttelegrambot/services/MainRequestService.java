package org.patientbot.patienttelegrambot.services;

import lombok.RequiredArgsConstructor;
import org.patientbot.patienttelegrambot.dtos.AuthenticateDTO;
import org.patientbot.patienttelegrambot.dtos.ChatStateDTO;
import org.patientbot.patienttelegrambot.exception.RestTemplateExceptionHandler;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MainRequestService {

    private final RestTemplate restTemplate;

    public Optional<ChatStateDTO> getChatState(long chatID) {
        ResponseEntity<ChatStateDTO> optionalChatState = restTemplate.getForEntity("http://localhost:8082/chat/api/chatstate/%s" .formatted(chatID), ChatStateDTO.class);
        System.out.println(optionalChatState.getStatusCode());
        return optionalChatState.getStatusCode().is2xxSuccessful() ?
                Optional.of(optionalChatState.getBody()) :
                Optional.empty();
    }

    public Optional<ChatStateDTO> createChatState(long chatID) {
        ResponseEntity<ChatStateDTO> optionalChatState = restTemplate.postForEntity("http://localhost:8082/chat/api/chatstate?chatID=%s" .formatted(chatID), null, ChatStateDTO.class);
        return optionalChatState.getStatusCode().is2xxSuccessful() ?
                Optional.of(optionalChatState.getBody()) :
                Optional.empty();
    }

    public Optional<AuthenticateDTO> getAuthenticationStatus(long chatID) {
        ResponseEntity<AuthenticateDTO> optionalAuthenticationStatus = restTemplate.getForEntity("http://localhost:8082/chat/api/authenticate/%s" .formatted(chatID), AuthenticateDTO.class);
        return optionalAuthenticationStatus.getStatusCode().is2xxSuccessful() ?
                Optional.of(optionalAuthenticationStatus.getBody()) :
                Optional.empty();
    }

}
