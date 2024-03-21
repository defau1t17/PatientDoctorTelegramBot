package org.telegrambots.doctortelegrambot.doctorCommandsHandlers;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegrambots.doctortelegrambot.entities.ChatState;
import org.telegrambots.doctortelegrambot.repositories.ChatStateRepository;

public interface StateUpdatable {
    static void updateState(ChatStateRepository chatStateRepository, ChatState chatState, String moveReference) {
        chatState.setChatStates(chatState.getChatStates().move(moveReference));
        chatStateRepository.save(chatState);
    }

    void responseOnState(ChatState chatState, Update update);
}
