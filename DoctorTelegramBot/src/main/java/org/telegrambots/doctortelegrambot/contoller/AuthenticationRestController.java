package org.telegrambots.doctortelegrambot.contoller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegrambots.doctortelegrambot.repository.PermissionRepository;
import org.telegrambots.doctortelegrambot.service.AuthenticationService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/authenticate")
@RequiredArgsConstructor
public class AuthenticationRestController {

    private final PermissionRepository permissionRepository;

    private final AuthenticationService authenticationService;

    @GetMapping
    public ResponseEntity<?> getAuthenticationStatus(int chatID) {
        return permissionRepository.findByChatID(chatID) == null || permissionRepository.findByChatID(chatID).getChatID() == -1 ?
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).build() :
                ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<?> authenticate(int chatID, UUID token) {
        return authenticationService.authenticate(chatID, token) ?
                ResponseEntity.ok().build() :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
