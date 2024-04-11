package org.telegramchat.chat.endpoints;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegramchat.chat.entity.TelegramBotAuthentication;
import org.telegramchat.chat.service.AuthenticationService;

import java.util.Optional;

@Tag(name = "Authentication Endpoint", description = "Manage Telegram user authentication")
@RestController
@RequestMapping("/authenticate")
@RequiredArgsConstructor
public class AuthenticationEndpoint {

    private final AuthenticationService service;


    @Operation(summary = "Get authentication page", tags = "GET", responses = {
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = TelegramBotAuthentication[].class)))
    })
    @GetMapping
    public ResponseEntity<?> getAuthenticationPage(@RequestParam(value = "pageNumber") Optional<Integer> pageNumber,
                                                   @RequestParam(value = "pageSize") Optional<Integer> pageSize) {
        return ResponseEntity
                .ok(service.findAll(pageNumber.orElse(0), pageSize.orElse(5)));
    }

    @Operation(summary = "Get authentication by chat ID", tags = "GET", parameters = @Parameter(name = "chatID", example = "122"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = TelegramBotAuthentication.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "404")
    })
    @GetMapping("/{chatID}")
    public ResponseEntity<?> getAuthenticationByChatID(@PathVariable("chatID") long chatID) {
        Optional<TelegramBotAuthentication> optional = service.findByChatID(chatID);
        return optional.isPresent() ?
                ResponseEntity
                        .ok(optional.get()) :
                ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .build();
    }

    @Operation(summary = "Update authentication", tags = "PATCH", parameters = {@Parameter(name = "chatID", example = "13"), @Parameter(name = "token", example = "8fb00b2d-1de8-4a72-a227-9557402de126")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TelegramBotAuthentication.class))),
            @ApiResponse(responseCode = "400")
    })
    @PatchMapping
    public ResponseEntity<?> authenticate(@RequestParam(value = "chatID") long chatID, @RequestParam(value = "token") String token) {
        Optional<TelegramBotAuthentication> optionalByChatID = service.findByChatID(chatID);
        Optional<TelegramBotAuthentication> optionalByToken = service.findAuthenticationByToken(token);
        return optionalByChatID.isEmpty() && optionalByToken.isPresent() ?
                ResponseEntity
                        .ok(service.authenticate(chatID, optionalByToken.get())) :
                ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .build();
    }

}
