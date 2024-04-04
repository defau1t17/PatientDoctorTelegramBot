package org.patientbot.patienttelegrambot.patientTelegramBotCommandsHandlers;

import org.patientbot.patienttelegrambot.dtos.AuthenticateDTO;
import org.patientbot.patienttelegrambot.dtos.ChatStateDTO;
import org.patientbot.patienttelegrambot.entity.ChatStates;
import org.patientbot.patienttelegrambot.entity.TelegramBotResponses;
import org.patientbot.patienttelegrambot.services.MainRequestService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;
import java.util.Optional;

@Component
public class CommandHandler implements Command {

    private Map<String, Command> commandMap;

    private Long CHAT_ID;

    private String message;

    private ChatStates chatState;

    private final MainRequestService requestService;


    public CommandHandler(AuthenticationCommandHandler authenticationCommandHandler,
                          DoctorCommandHandler doctorCommandHandler,
                          MainRequestService requestService,
                          EmergencyCommandHandler emergencyCommandHandler) {
        this.requestService = requestService;
        commandMap = Map.of(
                "/authenticate", authenticationCommandHandler,
                "/doctors", doctorCommandHandler,
                "/doctor_id", doctorCommandHandler,
                "/emergency", emergencyCommandHandler);
    }


    @Override
    public SendMessage sendResponse(Update update) {
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

        Command command = commandMap.get(message);
        if (command != null) {
            return command.sendResponse(update);
        } else if (command == null && !chatState.equals(ChatStates.DEFAULT)) {
            command = commandMap.get(chatState.getDescription());
            return command.sendResponse(update);
        } else {
            return new SendMessage(String.valueOf(CHAT_ID), TelegramBotResponses.SOME_ERROR.getDescription());
        }

    }
}
