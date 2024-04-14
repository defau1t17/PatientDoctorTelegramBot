package org.telegrambots.doctortelegrambot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegrambots.doctortelegrambot.dto.AuthenticateDTO;
import org.telegrambots.doctortelegrambot.dto.AuthenticatedUserDTO;
import org.telegrambots.doctortelegrambot.entities.ChatState;
import org.telegrambots.doctortelegrambot.entities.ChatStates;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:application.properties")
public class AuthenticationRequestService {

    @Value(value = "${chat.service.address}")
    private String chatService;
    private final RestTemplate restTemplate;

    public Optional<AuthenticateDTO> authenticate(long chatID, String token) {
        ResponseEntity<AuthenticateDTO> optionalAuthentication = restTemplate.exchange("http://%s:8082/chat/api/authenticate?chatID=%s&token=%s"
                .formatted(chatService, chatID, token), HttpMethod.PATCH, null, AuthenticateDTO.class);
        return optionalAuthentication.getStatusCode().is2xxSuccessful() ?
                Optional.of(optionalAuthentication.getBody()) :
                Optional.empty();
    }


    public Optional<AuthenticateDTO> getAuthenticationStatus(long chatID) {
        ResponseEntity<AuthenticateDTO> optionalAuthenticationStatus = restTemplate.getForEntity("http://%s:8082/chat/api/authenticate/%s"
                .formatted(chatService, chatID), AuthenticateDTO.class);
        return optionalAuthenticationStatus.getStatusCode().is2xxSuccessful() ?
                Optional.of(optionalAuthenticationStatus.getBody()) :
                Optional.empty();
    }


}
