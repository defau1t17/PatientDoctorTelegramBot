package org.telegrambots.doctortelegrambot.doctorCommandsHandlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegrambots.doctortelegrambot.dto.AuthenticateDTO;
import org.telegrambots.doctortelegrambot.dto.AuthenticatedUserDTO;
import org.telegrambots.doctortelegrambot.entities.ChatState;
import org.telegrambots.doctortelegrambot.entities.ChatStates;
import org.telegrambots.doctortelegrambot.entities.TelegramBotResponses;
import org.telegrambots.doctortelegrambot.services.AuthenticationRequestService;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthenticationCommandHandler implements Command, StateUpdatable {

    private final SendMessage sendMessage = new SendMessage();

    private final AuthenticationRequestService requestService;

    private String responseMessage = "";

    private long CHAT_ID = 0;


    @Override
    public SendMessage sendResponse(Update update) {
        CHAT_ID = update.getMessage().getChatId();
        Optional<ChatState> chatState = requestService.getChatState(CHAT_ID);

        if (chatState.isPresent()) {
            responseOnState(chatState.get(), update);
        } else {
            responseMessage = TelegramBotResponses.SOME_ERROR.getDescription();
        }

        sendMessage.setText(responseMessage);
        sendMessage.setChatId(update.getMessage().getChatId());
        return sendMessage;
    }

    @Override
    public void responseOnState(ChatState chatState, Update update) {
        String text = update.getMessage().getText();
        switch (chatState.getChatStates()) {
            case DEFAULT -> {
                Optional<ChatState> optionalUpdatedChatState = requestService.updateChatState(CHAT_ID, ChatStates.WAITING_FOR_TOKEN);
                if (optionalUpdatedChatState.isPresent()) {
                    this.responseMessage = "Please, write your personal access token \n(Avoid '@,|,/,!,#,$,%,^,&,*,(,),{,},[,]' symbols)\n";
                } else {
                    this.responseMessage = TelegramBotResponses.SOME_ERROR.getDescription();
                }
            }
            case WAITING_FOR_TOKEN -> {
                String token = update.getMessage().getText();
                Optional<AuthenticateDTO> optionalAuthenticate = requestService.authenticate(CHAT_ID, token);
                Optional<AuthenticatedUserDTO> updateDTO = requestService.updateChatIDInHospitalDatabase(CHAT_ID, token);
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
            default -> {
                Optional<ChatState> optionalUpdatedChatState = requestService.updateChatState(CHAT_ID, ChatStates.DEFAULT);
                if (optionalUpdatedChatState.isPresent()) {
                    responseOnState(chatState, update);
                } else {
                    this.responseMessage = TelegramBotResponses.SOME_ERROR.getDescription();
                }
            }
        }
    }

    @Override
    public ChatState moveChatState(long chatID) {
        return requestService.moveChatStateToNextState(chatID).get();
    }

}
