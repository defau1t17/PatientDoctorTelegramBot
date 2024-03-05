package org.telegrambots.doctortelegrambot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegrambots.doctortelegrambot.repositories.PermissionRepository;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final PermissionRepository permissionRepository;


}
