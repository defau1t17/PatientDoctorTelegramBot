package org.telegrambots.doctortelegrambot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.telegrambots.doctortelegrambot.entities.Permission;

import java.util.UUID;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Integer> {

    Permission findByChatID(int chatID);

    Permission findByPermissionToken(UUID token);
}
