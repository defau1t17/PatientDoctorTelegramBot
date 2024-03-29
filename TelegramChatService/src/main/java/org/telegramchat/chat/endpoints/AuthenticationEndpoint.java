package org.telegramchat.chat.endpoints;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegramchat.chat.entity.TelegramBotAuthentication;
import org.telegramchat.chat.service.AuthenticationService;

import java.util.Optional;

@RestController()
@RequestMapping("/authenticate")
@RequiredArgsConstructor
public class AuthenticationEndpoint {

    private final AuthenticationService service;

    @Cacheable(value = "pageWithAuthentication")
    @GetMapping
    public ResponseEntity<?> getAuthenticationPage(@RequestParam(value = "pageNumber") Optional<Integer> pageNumber,
                                                   @RequestParam(value = "pageSize") Optional<Integer> pageSize) {
        return ResponseEntity
                .ok(service.findAll(pageNumber.orElse(0), pageSize.orElse(5)));
    }

    @Cacheable("authenticationByChatID")
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
