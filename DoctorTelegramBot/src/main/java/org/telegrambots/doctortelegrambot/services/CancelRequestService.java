package org.telegrambots.doctortelegrambot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegrambots.doctortelegrambot.entities.ChatState;
import org.telegrambots.doctortelegrambot.entities.ChatStates;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CancelRequestService {

    private final RestTemplate restTemplate;

    public Optional<ChatState> getChatState(long chatID) {
        ResponseEntity<ChatState> optionalChatState = restTemplate.getForEntity("http://localhost:8082/chat/api/chatstate/%s"
                .formatted(chatID), ChatState.class);
        return optionalChatState.getStatusCode().is2xxSuccessful() ?
                Optional.of(optionalChatState.getBody()) :
                Optional.empty();
    }

    public Optional<ChatState> rollBackChatStateToDefault(long chatID) {
        ResponseEntity<ChatState> optionalUpdateChatState = restTemplate.postForEntity("http://localhost:8082/chat/api/chatstate/%s/update?state=%s"
                .formatted(chatID, ChatStates.DEFAULT.name()), null, ChatState.class);
        return optionalUpdateChatState.getStatusCode().is2xxSuccessful() ?
                Optional.of(optionalUpdateChatState.getBody()) :
                Optional.empty();
    }


}
