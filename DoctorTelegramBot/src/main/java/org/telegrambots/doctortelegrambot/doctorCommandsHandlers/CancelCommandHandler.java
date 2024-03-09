package org.telegrambots.doctortelegrambot.doctorCommandsHandlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegrambots.doctortelegrambot.entities.ChatState;
import org.telegrambots.doctortelegrambot.entities.ChatStates;
import org.telegrambots.doctortelegrambot.repositories.ChatStateRepository;

@Component
@RequiredArgsConstructor
public class CancelCommandHandler implements Command {
    private final SendMessage sendMessage = new SendMessage();
    private final ChatStateRepository chatStateRepository;
    private String responseMessage = "";

    @Override
    public SendMessage sendResponse(Update update) {
        ChatState chatState = chatStateRepository.findChatStateByChatID(Math.toIntExact(update.getMessage().getChatId())).orElse(null);
        if (chatState == null) {
            this.responseMessage = "Nothing to cancel";
        } else {
            rollbackStateToDefault(chatState);
        }
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText(responseMessage);
        return null;
    }

    private void rollbackStateToDefault(ChatState chatState) {
        this.responseMessage = "Operation %s has canceled".formatted(chatState.getChatStates().getCommandReference());
        chatState.setChatStates(ChatStates.DEFAULT);
        rollbackStateToDefault(chatState);
    }
}
