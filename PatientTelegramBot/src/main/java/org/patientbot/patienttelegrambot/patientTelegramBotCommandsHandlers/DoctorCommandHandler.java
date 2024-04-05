package org.patientbot.patienttelegrambot.patientTelegramBotCommandsHandlers;

import lombok.RequiredArgsConstructor;
import org.patientbot.patienttelegrambot.dtos.ChatStateDTO;
import org.patientbot.patienttelegrambot.dtos.DoctorDTO;
import org.patientbot.patienttelegrambot.dtos.PagebleDoctorDTO;
import org.patientbot.patienttelegrambot.entity.ChatStates;
import org.patientbot.patienttelegrambot.entity.TelegramBotResponses;
import org.patientbot.patienttelegrambot.services.DoctorRequestService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

import static java.util.Arrays.*;

@Component
@RequiredArgsConstructor
public class DoctorCommandHandler implements Command, State {

    private final DoctorRequestService doctorRequestService;

    private String responseMessage = "test";

    private long CHAT_ID = 0;

    private final String DOCTOR_ID_DIRECTION = "/doctor_id";
    private final String DOCTORS_DIRECTION = "/doctors";

    private int PAGE_NUMBER = 0;

    private final int PAGE_SIZE = 5;

    private int MAX_PAGE_NUMBER = 1;

    private SendMessage sendMessage = new SendMessage();

    private final DoctorsKeyboard doctorsKeyboard;

    @Override
    public SendMessage sendResponse(Update update) {
        if (update.hasMessage()) {
            CHAT_ID = update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            CHAT_ID = update.getCallbackQuery().getMessage().getChatId();
        }

        Optional<ChatStateDTO> chatState = doctorRequestService.getChatState(CHAT_ID);
        if (chatState.isPresent()) {
            responseOnState(ChatStates.valueOf(chatState.get().getChatStates()), update);
        } else {
            responseMessage = TelegramBotResponses.SOME_ERROR.getDescription();
        }
        sendMessage.setChatId(CHAT_ID);
        sendMessage.setText(responseMessage);
        return sendMessage;
    }

    @Override
    public void responseOnState(ChatStates chatState, Update update) {
        switch (chatState) {
            case DEFAULT -> {
                if (doctorRequestService.updateChatState(CHAT_ID,
                        stream(ChatStates.values())
                                .filter(chatStates -> update.getMessage().getText().equals(chatStates.getDescription()))
                                .findFirst()
                                .get()).isPresent()) {
                    if (update.getMessage().getText().equals(DOCTOR_ID_DIRECTION)) {
                        responseMessage = TelegramBotResponses.INPUT_DOCTOR_ID.getDescription();
                    } else {
                        Optional<PagebleDoctorDTO> doctors = doctorRequestService.getDoctors(PAGE_NUMBER, PAGE_SIZE);
                        responseMessage = doctors
                                .get()
                                .toString();
                        MAX_PAGE_NUMBER = doctors.get().getTotalPages() - 1;
                        sendMessage.setReplyMarkup(doctorsKeyboard.getDoctorsKeyboard(PAGE_NUMBER, MAX_PAGE_NUMBER));
                    }
                }
            }
            case WAITING_FOR_DOCTOR_ID -> {
                long doctorID = Long.parseLong(update.getMessage().getText());
                Optional<DoctorDTO> doctorByID = doctorRequestService.getDoctorByID(doctorID);
                responseMessage = doctorByID.isPresent() ?
                        doctorByID
                                .get()
                                .toString() :
                        TelegramBotResponses.NO_DOCTOR_WITH_SUCH_ID.getDescription();
                moveChatState(CHAT_ID);
            }
            case WAITING_FOR_PREVIOUS_OR_NEXT_COMMAND_DOCTORS -> {
                String moveDirection = update.getCallbackQuery().getData();
                switch (moveDirection) {
                    case "NEXT" -> {
                        PAGE_NUMBER++;
                        Optional<PagebleDoctorDTO> doctors = doctorRequestService.getDoctors(PAGE_NUMBER, PAGE_SIZE);
                        MAX_PAGE_NUMBER = doctors
                                .get()
                                .getTotalPages() - 1;
                        responseMessage = doctors
                                .get()
                                .toString();
                        sendMessage.setReplyMarkup(doctorsKeyboard.getDoctorsKeyboard(PAGE_NUMBER, MAX_PAGE_NUMBER));
                    }
                    case "BACK" -> {
                        PAGE_NUMBER--;
                        Optional<PagebleDoctorDTO> doctors = doctorRequestService.getDoctors(PAGE_NUMBER, PAGE_SIZE);
                        MAX_PAGE_NUMBER = doctors
                                .get()
                                .getTotalPages() - 1;
                        responseMessage = doctors
                                .get()
                                .toString();
                        sendMessage.setReplyMarkup(doctorsKeyboard.getDoctorsKeyboard(PAGE_NUMBER, MAX_PAGE_NUMBER));
                    }
                    case "STOP" -> {
                        doctorRequestService.updateChatState(CHAT_ID, ChatStates.DEFAULT);
                        sendMessage.setReplyMarkup(null);
                        responseMessage = "Operation '%s' has stopped".formatted(chatState.getDescription());
                    }
                }
            }
        }
    }

    @Override
    public void moveChatState(long CHAT_ID) {
        doctorRequestService.moveChatStateToNextState(CHAT_ID);
    }
}
