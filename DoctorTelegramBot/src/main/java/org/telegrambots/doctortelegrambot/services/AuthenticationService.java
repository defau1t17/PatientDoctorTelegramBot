package org.telegrambots.doctortelegrambot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegrambots.doctortelegrambot.entities.Permission;
import org.telegrambots.doctortelegrambot.repositories.PermissionRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final PermissionRepository permissionRepository;

    public boolean authenticate(int chatID, UUID token) {
        if (validateBeforeAuthentication(token)) {
            Permission permissionByToken = permissionRepository.findByPermissionToken(token);
            permissionByToken.setChatID(chatID);
            return permissionRepository.save(permissionByToken) != null;
        }
        return false;
    }

    public boolean unauthenticate(int chatID) {
        if (validateBeforeUnauthentication(chatID)) {
            Permission permissionByChatID = permissionRepository.findByChatID(chatID);
            permissionByChatID.setChatID(-1);
            return permissionRepository.save(permissionByChatID) != null;
        }
        return false;
    }

    public boolean validateBeforeAuthentication(UUID token) {
        Permission permissionByToken = permissionRepository.findByPermissionToken(token);
        return permissionByToken != null && permissionByToken.getChatID() == -1;
    }

    public boolean validateBeforeUnauthentication(int chatID) {
        Permission permissionByChatID = permissionRepository.findByChatID(chatID);
        return permissionByChatID != null;
    }

}
