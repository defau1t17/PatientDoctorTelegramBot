package org.telegrambots.doctortelegrambot.doctorCommandsHandlers;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegrambots.doctortelegrambot.entities.ChatState;

public interface StateUpdatable {

    void responseOnState(ChatState chatState, Update update);

    ChatState moveChatState(long chatID);


}
