package org.telegrambots.doctortelegrambot.doctorCommandsHandlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegrambots.doctortelegrambot.dto.PaginatedPatientsDTO;
import org.telegrambots.doctortelegrambot.dto.PatientDTO;
import org.telegrambots.doctortelegrambot.entities.ChatState;
import org.telegrambots.doctortelegrambot.entities.ChatStates;
import org.telegrambots.doctortelegrambot.entities.TelegramBotResponses;
import org.telegrambots.doctortelegrambot.services.PatientRequestService;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PatientsCommandHandler implements Command, StateUpdatable {

    private final SendMessage sendMessage = new SendMessage();

    private final PatientRequestService requestService;

    private final PatientsKeyboard patientsKeyboard;

    private String responseMessage = "";

    private int PAGE_NUMBER = 0;
    private final int PAGE_SIZE = 1;

    private int MAX_PAGE_NUMBER = 0;

    private final String PATIENT_MOVE_REFERENCE = "/patient";

    private long CHAT_ID = -1;

    @Override
    public SendMessage sendResponse(Update update) {
        if (update.hasMessage()) {
            CHAT_ID = update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            CHAT_ID = update.getCallbackQuery().getMessage().getChatId();
        }

        Optional<ChatState> optionalChatState = requestService.getChatState(CHAT_ID);

        if (optionalChatState.isPresent()) {
            responseOnState(optionalChatState.get(), update);
        } else {
            this.responseMessage = TelegramBotResponses.SOME_ERROR.getDescription();
        }
        sendMessage.setChatId(CHAT_ID);
        sendMessage.setText(responseMessage);

        return sendMessage;
    }

    @Override
    public void responseOnState(ChatState chatState, Update update) {
        switch (chatState.getChatStates()) {
            case DEFAULT -> {
                if (update.getMessage().getText().equals(PATIENT_MOVE_REFERENCE)) {
                    this.responseMessage = "Please, enter ID of the patient below : ";
                    requestService.updateChatState(CHAT_ID, ChatStates.WAITING_FOR_PATIENT_ID);
                } else {
                    PaginatedPatientsDTO paginatedPatients = requestService.getPaginatedPatients(PAGE_NUMBER, PAGE_SIZE);
                    this.responseMessage = paginatedPatients.toString();
                    sendMessage.setReplyMarkup(patientsKeyboard.getPatientsKeyboard(PAGE_NUMBER, MAX_PAGE_NUMBER));
                    requestService.updateChatState(CHAT_ID, ChatStates.WAITING_FOR_PREVIOUS_OR_NEXT_COMMAND);
                }
            }
            case WAITING_FOR_PATIENT_ID -> {
                try {
                    long optionalPatientID = Long.parseLong(update.getMessage().getText());
                    Optional<PatientDTO> patientByID = requestService.getPatientByID(optionalPatientID);
                    if (patientByID.isPresent()) {
                        this.responseMessage = patientByID.get().toString();
                    } else this.responseMessage = TelegramBotResponses.PATIENT_NOT_FOUNT.getDescription();
                    moveChatState(CHAT_ID);
                } catch (Exception e) {
                    this.responseMessage = TelegramBotResponses.SYNTAX_ERROR.getDescription();
                    responseOnState(chatState, update);
                }
            }
            case WAITING_FOR_PREVIOUS_OR_NEXT_COMMAND -> {
                String moveDirection = update.getCallbackQuery().getData();
                switch (moveDirection) {
                    case "NEXT" -> {
                        PAGE_NUMBER++;
                        PaginatedPatientsDTO paginatedPatients = requestService.getPaginatedPatients(PAGE_NUMBER, PAGE_SIZE);
                        MAX_PAGE_NUMBER = paginatedPatients
                                .getTotalPages() - 1;
                        this.responseMessage = paginatedPatients.toString();
                        sendMessage.setReplyMarkup(patientsKeyboard.getPatientsKeyboard(PAGE_NUMBER, MAX_PAGE_NUMBER));
                    }
                    case "BACK" -> {
                        PAGE_NUMBER--;
                        PaginatedPatientsDTO paginatedPatients = requestService.getPaginatedPatients(PAGE_NUMBER, PAGE_SIZE);
                        MAX_PAGE_NUMBER = paginatedPatients
                                .getTotalPages() - 1;
                        this.responseMessage = paginatedPatients.toString();
                        sendMessage.setReplyMarkup(patientsKeyboard.getPatientsKeyboard(PAGE_NUMBER, MAX_PAGE_NUMBER));
                    }
                    case "STOP" -> {
                        requestService.updateChatState(CHAT_ID, ChatStates.DEFAULT);
                        sendMessage.setReplyMarkup(null);
                        this.responseMessage = "Stopped";
                    }
                }
            }
            default -> {
                Optional<ChatState> optionalChatState = requestService.updateChatState(CHAT_ID, ChatStates.DEFAULT);
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
        return requestService.moveChatStateToNextState(chatID).get();
    }

}
