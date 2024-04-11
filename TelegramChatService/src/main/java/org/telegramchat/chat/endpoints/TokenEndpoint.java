package org.telegramchat.chat.endpoints;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegramchat.chat.entity.TelegramBotAuthentication;
import org.telegramchat.chat.repository.TelegramBotAuthenticationRepository;

@Tag(name = "Token Endpoint", description = "Manage Telegram authentication token")
@RestController
@RequestMapping("/token")
@RequiredArgsConstructor
public class TokenEndpoint {

    private final TelegramBotAuthenticationRepository repository;

    @Operation(summary = "Get generated authentication token", tags = "GET")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class, example = "dff49a52-f28c-449e-9ec7-c6e0dadbdb33")))
    @GetMapping
    public String generateToken() {
        return TelegramBotAuthentication.authenticationFabric(repository).getToken();
    }
}
