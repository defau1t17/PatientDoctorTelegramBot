package org.telegrambots.doctortelegrambot.doctorCommandsHandlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegrambots.doctortelegrambot.entities.ChatState;
import org.telegrambots.doctortelegrambot.entities.ChatStates;
import org.telegrambots.doctortelegrambot.entities.Permission;
import org.telegrambots.doctortelegrambot.entities.TelegramBotResponses;
import org.telegrambots.doctortelegrambot.repositories.ChatStateRepository;
import org.telegrambots.doctortelegrambot.repositories.PermissionRepository;

import java.util.Map;
import java.util.Optional;

@Component
public class CommandHandler {

    private final Map<String, Command> commands;

    @Autowired
    private ChatStateRepository chatStateRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    private long chatID = -1;

    private String message = "";

    private String command = "";

    private Command commandHandler = null;


    public CommandHandler(Map<String, Command> commands,
                          AuthenticationCommandHandler authenticationCommandHandler,
                          PatientsCommandHandler patientsCommandHandler,
                          ShiftCommandHandler shiftCommandHandler,
                          NewPatientCommandHandler newPatientCommandHandler,
                          CancelCommandHandler cancelCommandHandler) {
        this.commands = Map.of("/authenticate", authenticationCommandHandler,
                "/patients", patientsCommandHandler,
                "/patient", patientsCommandHandler,
                "/shift", shiftCommandHandler,
                "/new_patient", newPatientCommandHandler,
                "/cancel", cancelCommandHandler);
    }

    public SendMessage handleCommands(Update update) {
        if (update.hasMessage()) {
            System.out.println("message");
            message = update.getMessage().getText();
            command = message.split(" ")[0];
            chatID = update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            System.out.println("callback");
            message = update.getCallbackQuery().getMessage().getText();
            chatID = update.getCallbackQuery().getMessage().getChatId();
        }

        Optional<ChatState> optionalChatStateByChat = chatStateRepository.findChatStateByChatID(chatID);
        Permission chat = permissionRepository.findByChatID(chatID);
        commandHandler = commands.get(command);

        if (allowResourceAccess(command, chat, optionalChatStateByChat.orElse(null))) {
            if (commandHandler != null && allowResourceAccess(command, chat, optionalChatStateByChat.orElse(null))) {
                return commandHandler.sendResponse(update);
            } else if (optionalChatStateByChat.isPresent() && !optionalChatStateByChat.get().getChatStates().equals(ChatStates.DEFAULT)) {
                commandHandler = commands.get(optionalChatStateByChat.get().getChatStates().getCommandReference());
                return commandHandler.sendResponse(update);
            } else {
                return new SendMessage(String.valueOf(chatID), TelegramBotResponses.SYNTAX_ERROR.getDescription());
            }
        } else {
            return new SendMessage(String.valueOf(chatID), TelegramBotResponses.PERMISSION_DENIED_BECAUSE_OF_AUTHENTICATION.getDescription());
        }
    }

    private boolean allowResourceAccess(String resource, Permission permission, ChatState chatState) {
        if (!resource.equals("/authenticate") && permission == null && chatState == null) return false;
        if (!resource.equals("/authenticate") && permission == null && !chatState.getChatStates().equals(ChatStates.WAITING_FOR_TOKEN))
            return false;
        else return true;
    }
}
