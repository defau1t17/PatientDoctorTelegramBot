package org.telegrambots.doctortelegrambot.doctorCommandsHandlers;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegrambots.doctortelegrambot.dto.AuthenticateDTO;
import org.telegrambots.doctortelegrambot.dto.ChatStateDTO;
import org.telegrambots.doctortelegrambot.entities.ChatStates;
import org.telegrambots.doctortelegrambot.entities.TelegramBotResponses;
import org.telegrambots.doctortelegrambot.services.MainRequestService;

import java.util.Map;
import java.util.Optional;

@Component
public class CommandHandler {

    private final Map<String, Command> commands;

    private final MainRequestService requestService;

    private ChatStates chatState;

    private long CHAT_ID = -1;

    private String message = "";


    public CommandHandler(Map<String, Command> commands,
                          AuthenticationCommandHandler authenticationCommandHandler,
                          PatientsCommandHandler patientsCommandHandler,
                          ShiftCommandHandler shiftCommandHandler,
                          NewPatientCommandHandler newPatientCommandHandler,
                          CancelCommandHandler cancelCommandHandler,
                          MainRequestService mainRequestService) {
        this.requestService = mainRequestService;
        this.commands = Map.of("/authenticate", authenticationCommandHandler,
                "/patients", patientsCommandHandler,
                "/patient", patientsCommandHandler,
                "/shift", shiftCommandHandler,
                "/new_patient", newPatientCommandHandler,
                "/cancel", cancelCommandHandler);
    }

    public SendMessage handleCommands(Update update) {
        if (update.hasCallbackQuery()) {
            CHAT_ID = update.getCallbackQuery().getMessage().getChatId();
            message = update.getCallbackQuery().getMessage().getText();
        } else if (update.hasMessage()) {
            CHAT_ID = update.getMessage().getChatId();
            message = update.getMessage().getText();
        }

        Optional<ChatStateDTO> optionalChatState = requestService.getChatState(CHAT_ID);
        if (optionalChatState.isPresent()) {
            chatState = ChatStates.valueOf(optionalChatState.get().getChatStates());
        } else {
            Optional<ChatStateDTO> newChatState = requestService.createChatState(CHAT_ID);
            if (newChatState.isPresent()) {
                chatState = ChatStates.valueOf(newChatState.get().getChatStates());
            } else {
                return new SendMessage(String.valueOf(CHAT_ID), TelegramBotResponses.SOME_ERROR.getDescription());
            }
        }

        Optional<AuthenticateDTO> authenticationStatus = requestService.getAuthenticationStatus(CHAT_ID);
        if (authenticationStatus.isEmpty() && !chatState.equals(ChatStates.WAITING_FOR_TOKEN) && !message.equals("/authenticate")) {
            return new SendMessage(String.valueOf(CHAT_ID), TelegramBotResponses.PERMISSION_DENIED_BECAUSE_OF_AUTHENTICATION.getDescription());
        }

        Command command = commands.get(message);
        if (command != null) {
            return command.sendResponse(update);
        } else if (command == null && !chatState.equals(ChatStates.DEFAULT)) {
            command = commands.get(chatState.getCommandReference());
            return command.sendResponse(update);
        } else {
            return new SendMessage(String.valueOf(CHAT_ID), TelegramBotResponses.SOME_ERROR.getDescription());
        }
    }
}
