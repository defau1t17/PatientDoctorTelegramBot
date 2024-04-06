package org.telegrambots.doctortelegrambot.doctorCommandsHandlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegrambots.doctortelegrambot.dto.NewPatientDTO;
import org.telegrambots.doctortelegrambot.dto.PatientDTO;
import org.telegrambots.doctortelegrambot.entities.*;
import org.telegrambots.doctortelegrambot.services.ChatStateRequestService;
import org.telegrambots.doctortelegrambot.services.PatientRequestService;

import java.util.Optional;


@Component
@RequiredArgsConstructor
public class NewPatientCommandHandler implements Command, StateUpdatable {
    private final SendMessage sendMessage = new SendMessage();

    private final PatientRequestService requestService;

    private final ChatStateRequestService chatStateRequestService;

    private NewPatientDTO newPatientDTO = new NewPatientDTO();

    private String responseMessage = "";


    private long CHAT_ID = 0;

    @Override
    public SendMessage sendResponse(Update update) {
        CHAT_ID = update.getMessage().getChatId();
        Optional<ChatState> chatState = chatStateRequestService.getChatState(CHAT_ID);
        if (chatState.isPresent()) {
            responseOnState(chatState.get(), update);
        } else {
            this.responseMessage = TelegramBotResponses.SOME_ERROR.getDescription();
        }
        sendMessage.setText(responseMessage);
        sendMessage.setChatId(CHAT_ID);
        return sendMessage;
    }

    @Override
    public void responseOnState(ChatState chatState, Update update) {
        String message = update.getMessage().getText();
        switch (chatState.getChatStates()) {
            case DEFAULT -> {
                this.responseMessage = "Please write patient name below : ";
                chatStateRequestService.updateChatState(CHAT_ID, ChatStates.WAITING_FOR_NAME);
            }
            case WAITING_FOR_NAME -> {
                this.newPatientDTO.setName(message);
                this.responseMessage = "Please write patient second name below : ";
                moveChatState(CHAT_ID);
            }
            case WAITING_FOR_SECONDNAME -> {
                this.newPatientDTO.setSecondName(message);
                this.responseMessage = "Please describe patient disease : ";
                moveChatState(CHAT_ID);

            }
            case WAITING_FOR_DISEASE -> {
                this.newPatientDTO.setDisease(message);
                this.responseMessage = "Please input one of the states [STABLE, HARD, CRITICAL] : ";
                moveChatState(CHAT_ID);
            }
            case WAITING_FOR_PATIENT_STATE -> {
                try {
                    this.newPatientDTO.setPatientState(PatientState.valueOf(message.toUpperCase()));
                    this.responseMessage = "Please input the chamber number in range [10-999] : ";
                    moveChatState(CHAT_ID);
                } catch (Exception e) {
                    this.responseMessage = TelegramBotResponses.SYNTAX_ERROR.getDescription();
                    responseOnState(chatState, update);
                }
            }
            case WAITING_FOR_CHAMBER_NUMBER -> {
                try {
                    this.newPatientDTO.setChamberNumber(Integer.parseInt(message));
                    this.responseMessage = "You may add some description(if not just print '-') : ";
                    moveChatState(CHAT_ID);
                } catch (Exception e) {
                    this.responseMessage = TelegramBotResponses.SYNTAX_ERROR.getDescription()
                            .concat("Please input the chamber number in range [10-999] : ");
                    responseOnState(chatState, update);
                }
            }
            case WAITING_FOR_DESCRIPTION -> {
                this.newPatientDTO.setDescription(message);
                this.newPatientDTO.setToken(requestService.createPatientToken());
                Optional<PatientDTO> optionalPatient = requestService.createNewPatient(newPatientDTO);

                if (optionalPatient.isPresent()) {
                    this.responseMessage = "New patient to be created : \n%s".formatted(newPatientDTO.toString());
                } else {
                    this.responseMessage = "An error happened while validating a new patient!\n Unfortunately we can't save this patient";
                }
                moveChatState(CHAT_ID);
                newPatientDTO = new NewPatientDTO();
            }
            default -> {
                Optional<ChatState> optionalChatState = chatStateRequestService.updateChatState(CHAT_ID, ChatStates.DEFAULT);
                if (optionalChatState.isPresent()) {
                    responseOnState(chatState, update);
                } else {
                    this.responseMessage = TelegramBotResponses.SOME_ERROR.getDescription();
                }
            }
        }
    }

    @Override
    public ChatState moveChatState(long chatID) {
        return chatStateRequestService.moveChatStateToNextState(chatID).get();
    }
}
