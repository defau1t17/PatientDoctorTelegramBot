package org.telegrambots.doctortelegrambot.stateResolvers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegrambots.doctortelegrambot.entities.ChatState;

import java.util.Optional;

public interface ChatStateMovable {
    SendMessage responseOnState(ChatState chatState, Update update);

    Optional<ChatState> moveChatState(long chatID);

}
