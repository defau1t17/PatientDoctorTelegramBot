package org.telegrambots.doctortelegrambot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegrambots.doctortelegrambot.entity.Permission;
import org.telegrambots.doctortelegrambot.repository.PermissionRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final PermissionRepository permissionRepository;

    public boolean authenticate(int chatID, UUID token) {
        if (validate(chatID, token)) {
            Permission permissionByToken = permissionRepository.findByPermissionToken(token);
            permissionByToken.setChatID(chatID);
            return permissionRepository.save(permissionByToken) != null;
        }
        return false;
    }

    public boolean validate(int chatID, UUID token) {
        Permission permissionByToken = permissionRepository.findByPermissionToken(token);
        if (permissionByToken == null) return false;
        if (permissionByToken.getChatID() != -1) return false;
        return true;
    }

}
