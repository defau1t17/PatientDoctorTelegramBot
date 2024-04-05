package org.patientbot.patienttelegrambot.patientTelegramBotCommandsHandlers;

import org.patientbot.patienttelegrambot.entity.ChatStates;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface State {

    void responseOnState(ChatStates chatState, Update update);

    void moveChatState(long CHAT_ID);

}
