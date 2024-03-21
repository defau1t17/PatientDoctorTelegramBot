package org.telegrambots.doctortelegrambot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.telegrambots.doctortelegrambot.entities.ChatState;

import java.util.Optional;

@Repository
public interface ChatStateRepository extends JpaRepository<ChatState, Integer> {

    @Query(value = "SELECT state FROM ChatState state WHERE state.chat_id = ?1")
    Optional<ChatState> findChatStateByChatID(long chat_id);

}
