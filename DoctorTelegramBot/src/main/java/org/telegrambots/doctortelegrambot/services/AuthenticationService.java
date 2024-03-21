package org.telegrambots.doctortelegrambot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.telegrambots.doctortelegrambot.entities.Permission;
import org.telegrambots.doctortelegrambot.repositories.PermissionRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final PermissionRepository permissionRepository;


    @Transactional
    public boolean authenticate(long chatID, UUID token) {
        if (validateBeforeAuthentication(token)) {
            Permission permissionByToken = permissionRepository.findByPermissionToken(token);
            permissionByToken.setChatID(chatID);
            return permissionRepository.save(permissionByToken) != null;
        }
        return false;
    }

    @Transactional
    public boolean unauthenticate(long chatID) {
        if (validateBeforeUnauthentication(chatID)) {
            Permission permissionByChatID = permissionRepository.findByChatID(chatID);
            permissionByChatID.setChatID(-1);
            return permissionRepository.save(permissionByChatID) != null;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public boolean validateBeforeAuthentication(UUID token) {
        Permission permissionByToken = permissionRepository.findByPermissionToken(token);
        return permissionByToken != null && permissionByToken.getChatID() == -1;
    }

    @Transactional(readOnly = true)
    public boolean validateBeforeUnauthentication(long chatID) {
        Permission permissionByChatID = permissionRepository.findByChatID(chatID);
        return permissionByChatID != null;
    }

}
