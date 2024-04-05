package org.telegrambots.doctortelegrambot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegrambots.doctortelegrambot.dto.AuthenticateDTO;
import org.telegrambots.doctortelegrambot.dto.ChatStateDTO;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MainRequestService {

    private final RestTemplate restTemplate;

    public Optional<ChatStateDTO> getChatState(long chatID) {
        ResponseEntity<ChatStateDTO> optionalChatState = restTemplate.getForEntity("http://localhost:8082/chat/api/chatstate/%s" .formatted(chatID), ChatStateDTO.class);
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
