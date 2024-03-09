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
        String messageText = update.getMessage().getText();
        String command = messageText.split(" ")[0];
        long chatId = update.getMessage().getChatId();
        Optional<ChatState> optionalChatStateByChat = chatStateRepository.findChatStateByChatID(Math.toIntExact(update.getMessage().getChatId()));
        var commandHandler = commands.get(command);
        if (commandHandler != null) {
            System.out.println("command block works");
            return commandHandler.sendResponse(update);
        } else if (optionalChatStateByChat.isPresent() && !optionalChatStateByChat.get().getChatStates().equals(ChatStates.DEFAULT)) {
            System.out.println("state block works");

            commandHandler = commands.get(optionalChatStateByChat.get().getChatStates().getCommandReference());
            return commandHandler.sendResponse(update);
        } else {
            return new SendMessage(String.valueOf(chatId), "Error");
        }
    }


}
