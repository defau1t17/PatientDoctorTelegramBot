package org.telegrambots.doctortelegrambot.doctorCommandsHandlers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegrambots.doctortelegrambot.entities.ChatState;
import org.telegrambots.doctortelegrambot.entities.ChatStates;
import org.telegrambots.doctortelegrambot.repositories.ChatStateRepository;

import java.util.Map;
import java.util.Optional;

@Component
public class CommandHandler {

    private final Map<String, Command> commands;

    @Autowired
    private ChatStateRepository chatStateRepository;

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
            message = update.getMessage().getText();
            command = message.split(" ")[0];
            chatID = update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            message = update.getCallbackQuery().getMessage().getText();
            chatID = update.getCallbackQuery().getMessage().getChatId();
        }
        Optional<ChatState> optionalChatStateByChat = chatStateRepository.findChatStateByChatID(chatID);
        commandHandler = commands.get(command);
        if (commandHandler != null) {
            return commandHandler.sendResponse(update);
        } else if (optionalChatStateByChat.isPresent() && !optionalChatStateByChat.get().getChatStates().equals(ChatStates.DEFAULT)) {
            commandHandler = commands.get(optionalChatStateByChat.get().getChatStates().getCommandReference());
            return commandHandler.sendResponse(update);
        } else {
            return new SendMessage(String.valueOf(chatID), "Error");
        }
    }
}
