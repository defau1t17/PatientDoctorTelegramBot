package org.telegrambots.doctortelegrambot.contollers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegrambots.doctortelegrambot.repositories.PermissionRepository;
import org.telegrambots.doctortelegrambot.services.AuthenticationService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/authenticate")
@RequiredArgsConstructor
public class AuthenticationRestController {

    private final PermissionRepository permissionRepository;

    private final AuthenticationService authenticationService;

    @GetMapping
    public ResponseEntity<?> getAuthenticationStatus(@RequestParam long chatID) {
        return permissionRepository.findByChatID(chatID) == null || permissionRepository.findByChatID(chatID).getChatID() == -1 ?
                ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .build() :
                ResponseEntity
                        .ok()
                        .build();
    }

    @PostMapping
    public ResponseEntity<?> authenticate(@RequestParam int chatID, @RequestParam UUID token) {
        return authenticationService.authenticate(chatID, token) ?
                ResponseEntity
                        .ok()
                        .build() :
                ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .build();
    }

    @DeleteMapping
    public ResponseEntity<?> unauthenticate(@RequestParam long chatID) {
        return authenticationService.unauthenticate(chatID) ?
                ResponseEntity
                        .ok()
                        .build() :
                ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .build();
    }
}
