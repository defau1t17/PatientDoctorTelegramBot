package org.telegramchat.chat.endpoints;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegramchat.chat.entity.TelegramBotAuthentication;
import org.telegramchat.chat.repository.TelegramBotAuthenticationRepository;

@RestController
@RequestMapping("/token")
@RequiredArgsConstructor
public class TokenEndpoint {

    private final TelegramBotAuthenticationRepository repository;

    @GetMapping
    public String generateToken() {
        return TelegramBotAuthentication.authenticationFabric(repository).getToken();
    }
}
