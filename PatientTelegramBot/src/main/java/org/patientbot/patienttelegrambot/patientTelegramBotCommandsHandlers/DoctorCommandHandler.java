package org.patientbot.patienttelegrambot.patientTelegramBotCommandsHandlers;

import lombok.RequiredArgsConstructor;
import org.patientbot.patienttelegrambot.entity.ChatState;
import org.patientbot.patienttelegrambot.entity.TelegramBotResponses;
import org.patientbot.patienttelegrambot.services.ChatStateRequestService;
import org.patientbot.patienttelegrambot.stateResolvers.DoctorStateResolver;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;


@Component
@RequiredArgsConstructor
public class DoctorCommandHandler implements Command {

    private final DoctorStateResolver stateResolver;

    private final ChatStateRequestService chatStateRequestService;
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
            return stateResolver.responseOnState(chatState.get(), update);
        } else {
            return new SendMessage(String.valueOf(CHAT_ID), TelegramBotResponses.SOME_ERROR.getDescription());
        }
    }

}
