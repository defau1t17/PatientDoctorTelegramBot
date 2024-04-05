package org.telegramchat.chat.repository;

import org.telegramchat.chat.entity.TelegramBotAuthentication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TelegramBotAuthenticationRepository extends JpaRepository<TelegramBotAuthentication, Integer> {
    Optional<TelegramBotAuthentication> findTelegramBotAuthenticationByChatID(long chatID);

    void deleteTelegramBotAuthenticationByChatID(long chatID);

    Optional<TelegramBotAuthentication> findTelegramBotAuthenticationByToken(String token);

}
