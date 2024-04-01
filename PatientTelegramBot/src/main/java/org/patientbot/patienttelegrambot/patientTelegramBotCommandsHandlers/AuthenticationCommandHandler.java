package org.patientbot.patienttelegrambot.patientTelegramBotCommandsHandlers;

import lombok.RequiredArgsConstructor;
import org.patientbot.patienttelegrambot.dtos.AuthenticateDTO;
import org.patientbot.patienttelegrambot.dtos.AuthenticatedUserDTO;
import org.patientbot.patienttelegrambot.dtos.ChatStateDTO;
import org.patientbot.patienttelegrambot.entity.ChatStates;
import org.patientbot.patienttelegrambot.services.AuthenticationRequestService;
import org.patientbot.patienttelegrambot.entity.TelegramBotResponses;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthenticationCommandHandler implements Command, State {

    private final AuthenticationRequestService authenticationRequestService;

    private final SendMessage sendMessage = new SendMessage();

    private String responseMessage = "test";

    private long CHAT_ID = 0;

    @Override
    public SendMessage sendResponse(Update update) {
        Optional<ChatStateDTO> optionalChatStates = authenticationRequestService.getChatState(update.getMessage().getChatId());
        if (optionalChatStates.isPresent()) {
            responseOnState(ChatStates.valueOf(optionalChatStates.get().getChatStates()), update);
        } else {
            responseMessage = TelegramBotResponses.SOME_ERROR.getDescription();
        }
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText(responseMessage);
        return sendMessage;
    }

    @Override
    public void responseOnState(ChatStates chatState, Update update) {
        CHAT_ID = update.getMessage().getChatId();
        switch (chatState) {
            case DEFAULT -> {
                if (authenticationRequestService.updateChatState(update.getMessage().getChatId(), ChatStates.WAITING_FOR_TOKEN).isPresent()) {
                    this.responseMessage = TelegramBotResponses.INPUT_TOKEN_DESCRIPTION.getDescription();
                } else {
                    responseMessage = TelegramBotResponses.SOME_ERROR.getDescription();
                }
            }
            case WAITING_FOR_TOKEN -> {
                String token = update.getMessage().getText();
                Optional<AuthenticateDTO> optionalAuthenticate = authenticationRequestService.authenticate(CHAT_ID, token);
                Optional<AuthenticatedUserDTO> updateDTO = authenticationRequestService.updateChatIDInHospitalDatabase(CHAT_ID, token);
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
    }

    @Override
    public void moveChatState(long CHAT_ID) {
        authenticationRequestService.moveChatStateToNextState(CHAT_ID);
    }

}
