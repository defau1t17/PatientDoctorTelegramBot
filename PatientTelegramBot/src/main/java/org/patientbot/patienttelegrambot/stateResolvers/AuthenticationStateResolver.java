package org.patientbot.patienttelegrambot.stateResolvers;

import lombok.RequiredArgsConstructor;
import org.patientbot.patienttelegrambot.dtos.AuthenticateDTO;
import org.patientbot.patienttelegrambot.dtos.AuthenticatedUserDTO;
import org.patientbot.patienttelegrambot.dtos.ChatStateDTO;
import org.patientbot.patienttelegrambot.entity.ChatState;
import org.patientbot.patienttelegrambot.entity.TelegramBotResponses;
import org.patientbot.patienttelegrambot.entity.ChatStates;
import org.patientbot.patienttelegrambot.services.AuthenticationRequestService;
import org.patientbot.patienttelegrambot.services.ChatStateRequestService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;


@Component
@RequiredArgsConstructor
public class AuthenticationStateResolver implements ChatStateMovable {

    private final AuthenticationRequestService authenticationRequestService;

    private final ChatStateRequestService chatStateRequestService;

    private final SendMessage sendMessage = new SendMessage();

    private String responseMessage = "test";

    private long CHAT_ID = 0;


    @Override
    public SendMessage responseOnState(ChatState chatState, Update update) {
        if (update.hasMessage()) {
            CHAT_ID = update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            CHAT_ID = update.getCallbackQuery().getMessage().getChatId();
        }

        switch (chatState.getChatStates()) {
            case DEFAULT -> {
                if (chatStateRequestService.updateChatState(CHAT_ID, ChatStates.WAITING_FOR_TOKEN).isPresent()) {
                    this.responseMessage = TelegramBotResponses.INPUT_TOKEN_DESCRIPTION.getDescription();
                } else {
                    responseMessage = TelegramBotResponses.SOME_ERROR.getDescription();
                }
            }
            case WAITING_FOR_TOKEN -> {
                String token = update.getMessage().getText();
                Optional<AuthenticateDTO> optionalAuthenticate = authenticationRequestService.authenticate(CHAT_ID, token);
                Optional<AuthenticatedUserDTO> updateDTO = chatStateRequestService.updateChatIDInHospitalDatabase(CHAT_ID, token);
                if (optionalAuthenticate.isPresent() && updateDTO.isPresent()) {
                    moveChatState(CHAT_ID);
                    responseMessage = "%s\nWelcome %s %s"
                            .formatted(TelegramBotResponses.AUTH_PASSED.getDescription(), updateDTO.get().getName(), updateDTO.get().getSecondName());
                } else if (optionalAuthenticate.isEmpty()) {
                    responseMessage = TelegramBotResponses.BAD_CREDS.getDescription();
                } else {
                    responseMessage = TelegramBotResponses.SYNTAX_ERROR.getDescription();
                }
            }
        }

        sendMessage.setChatId(CHAT_ID);
        sendMessage.setText(responseMessage);

        return sendMessage;
    }

    @Override
    public Optional<ChatState> moveChatState(long chatID) {
        return chatStateRequestService.moveChatStateToNextState(CHAT_ID);
    }
}
