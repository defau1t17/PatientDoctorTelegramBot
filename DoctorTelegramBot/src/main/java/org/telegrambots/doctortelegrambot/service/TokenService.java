package org.telegrambots.doctortelegrambot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegrambots.doctortelegrambot.repository.PermissionRepository;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final PermissionRepository permissionRepository;


}
