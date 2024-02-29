package org.telegrambots.doctortelegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.telegrambots.doctortelegrambot.entity.PermissionToken;

@Repository
public interface TokenRepository extends JpaRepository<PermissionToken, Integer> {
}
