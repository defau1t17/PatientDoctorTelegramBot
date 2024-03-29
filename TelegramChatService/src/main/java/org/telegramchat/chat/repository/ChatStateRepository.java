package org.telegramchat.chat.repository;

import org.telegramchat.chat.entity.ChatState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatStateRepository extends JpaRepository<ChatState, Integer> {

    Optional<ChatState> findChatStateByChatID(long chatID);

    void deleteChatStateByChatID(long chatID);
}
