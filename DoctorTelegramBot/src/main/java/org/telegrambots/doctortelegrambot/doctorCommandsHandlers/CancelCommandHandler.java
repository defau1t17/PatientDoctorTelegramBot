package org.telegrambots.doctortelegrambot.doctorCommandsHandlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegrambots.doctortelegrambot.entities.ChatState;
import org.telegrambots.doctortelegrambot.entities.ChatStates;
import org.telegrambots.doctortelegrambot.entities.TelegramBotResponses;
import org.telegrambots.doctortelegrambot.services.ChatStateRequestService;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CancelCommandHandler implements Command {


    private final ChatStateRequestService chatStateRequestService;
    private final SendMessage sendMessage = new SendMessage();
    private String responseMessage = "";
    private long CHAT_ID = 0;

    @Override
    public SendMessage sendResponse(Update update) {
        CHAT_ID = update.getMessage().getChatId();

        Optional<ChatState> chatState = chatStateRequestService.getChatState(CHAT_ID);

        if (chatState.isPresent()) {
            if (chatState.get().getChatStates().equals(ChatStates.DEFAULT))
                this.responseMessage = "Nothing to cancel";
            else {
                Optional<ChatState> optionalChatState = chatStateRequestService.rollBackChatStateToDefault(CHAT_ID);
                if (optionalChatState.isPresent()) {
                    this.responseMessage = "Operation %s has canceled".formatted(chatState.get().getChatStates().getCommandReference());
                } else {
                    this.responseMessage = TelegramBotResponses.SOME_ERROR.getDescription();
                }
            }
        } else {
            this.responseMessage = TelegramBotResponses.SOME_ERROR.getDescription();
        }
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText(responseMessage);
        return sendMessage;
    }
}
