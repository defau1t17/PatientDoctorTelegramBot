package org.telegrambots.doctortelegrambot.doctorCommandsHandlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegrambots.doctortelegrambot.entities.ChatState;
import org.telegrambots.doctortelegrambot.entities.TelegramBotResponses;
import org.telegrambots.doctortelegrambot.services.ChatStateRequestService;
import org.telegrambots.doctortelegrambot.stateResolvers.PatientStateResolver;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PatientsCommandHandler implements Command {

    private final ChatStateRequestService chatStateRequestService;
    private long CHAT_ID = -1;

    private final PatientStateResolver stateResolver;

    @Override
    public SendMessage sendResponse(Update update) {
        if (update.hasMessage()) {
            CHAT_ID = update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            CHAT_ID = update.getCallbackQuery().getMessage().getChatId();
        }

        Optional<ChatState> optionalChatState = chatStateRequestService.getChatState(CHAT_ID);

        if (optionalChatState.isPresent()) {
            return stateResolver.responseOnState(optionalChatState.get(), update);
        } else {
            return new SendMessage(String.valueOf(CHAT_ID), TelegramBotResponses.SOME_ERROR.getDescription());
        }
    }

}
