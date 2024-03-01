package org.telegrambots.doctortelegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.telegrambots.doctortelegrambot.entity.Permission;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Integer> {

    Permission findByChatID(int chatID);

    Permission findByPermissionToken(UUID token);
}
