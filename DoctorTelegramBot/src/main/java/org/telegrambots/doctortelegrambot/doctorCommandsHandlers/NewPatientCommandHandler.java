package org.telegrambots.doctortelegrambot.doctorCommandsHandlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegrambots.doctortelegrambot.entities.*;
import org.telegrambots.doctortelegrambot.services.ChatStateRequestService;
import org.telegrambots.doctortelegrambot.stateResolvers.NewPatientStateResolver;

import java.util.Optional;


@Component
@RequiredArgsConstructor
public class NewPatientCommandHandler implements Command {

    private final ChatStateRequestService chatStateRequestService;
    private final NewPatientStateResolver patientStateResolver;
    private long CHAT_ID = 0;

    @Override
    public SendMessage sendResponse(Update update) {

        if (update.hasMessage()) {
            CHAT_ID = update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            CHAT_ID = update.getCallbackQuery().getMessage().getChatId();
        }

        Optional<ChatState> chatState = chatStateRequestService.getChatState(CHAT_ID);
        if (chatState.isPresent()) {
            return patientStateResolver.responseOnState(chatState.get(), update);
        } else {
            return new SendMessage(String.valueOf(CHAT_ID), TelegramBotResponses.SOME_ERROR.getDescription());
        }
    }


}
