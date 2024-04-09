package org.patientbot.patienttelegrambot.stateResolvers;

import org.patientbot.patienttelegrambot.entity.ChatState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

public interface ChatStateMovable {
    SendMessage responseOnState(ChatState chatState, Update update);

    Optional<ChatState> moveChatState(long chatID);

}
