package org.patientbot.patienttelegrambot.patientTelegramBotCommandsHandlers;

import lombok.RequiredArgsConstructor;
import org.patientbot.patienttelegrambot.entity.ChatState;
import org.patientbot.patienttelegrambot.entity.TelegramBotResponses;
import org.patientbot.patienttelegrambot.services.ChatStateRequestService;
import org.patientbot.patienttelegrambot.stateResolvers.AuthenticationStateResolver;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthenticationCommandHandler implements Command {

    private final ChatStateRequestService chatStateRequestService;

    private final AuthenticationStateResolver stateResolver;
    private long CHAT_ID = 0;

    @Override
    public SendMessage sendResponse(Update update) {

        if (update.hasMessage()) {
            CHAT_ID = update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            CHAT_ID = update.getCallbackQuery().getMessage().getChatId();
        }

        Optional<ChatState> optionalChatStates = chatStateRequestService.getChatState(update.getMessage().getChatId());
        if (optionalChatStates.isPresent()) {
            return stateResolver.responseOnState(optionalChatStates.get(), update);
        } else {
            return new SendMessage(String.valueOf(CHAT_ID), TelegramBotResponses.SOME_ERROR.getDescription());
        }
    }


}
