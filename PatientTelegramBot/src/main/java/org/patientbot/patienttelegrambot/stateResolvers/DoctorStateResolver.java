package org.patientbot.patienttelegrambot.stateResolvers;

import lombok.RequiredArgsConstructor;
import org.patientbot.patienttelegrambot.dtos.DoctorDTO;
import org.patientbot.patienttelegrambot.dtos.PagebleDoctorDTO;
import org.patientbot.patienttelegrambot.entity.ChatState;
import org.patientbot.patienttelegrambot.entity.ChatStates;
import org.patientbot.patienttelegrambot.entity.TelegramBotResponses;
import org.patientbot.patienttelegrambot.patientTelegramBotCommandsHandlers.DoctorsKeyboard;
import org.patientbot.patienttelegrambot.services.ChatStateRequestService;
import org.patientbot.patienttelegrambot.services.DoctorRequestService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

import static java.util.Arrays.stream;

@Component
@RequiredArgsConstructor
public class DoctorStateResolver implements ChatStateMovable {

    private final DoctorRequestService doctorRequestService;

    private final ChatStateRequestService chatStateRequestService;

    private String responseMessage = "test";

    private long CHAT_ID = 0;

    private final String DOCTOR_ID_DIRECTION = "/doctor_id";

    private int PAGE_NUMBER = 0;

    private final int PAGE_SIZE = 5;

    private int MAX_PAGE_NUMBER = 1;

    private SendMessage sendMessage = new SendMessage();

    private final DoctorsKeyboard doctorsKeyboard;

    @Override
    public SendMessage responseOnState(ChatState chatState, Update update) {
        if (update.hasCallbackQuery()) {
            this.CHAT_ID = update.getCallbackQuery().getMessage().getChatId();
        } else if (update.hasMessage()) {
            this.CHAT_ID = update.getMessage().getChatId();
        }
        switch (chatState.getChatStates()) {
            case DEFAULT -> {
                if (chatStateRequestService.updateChatState(CHAT_ID,
                        stream(ChatStates.values())
                                .filter(chatStates -> update.getMessage().getText().equals(chatStates.getDescription()))
                                .findFirst()
                                .get()).isPresent()) {
                    if (update.getMessage().getText().equals(DOCTOR_ID_DIRECTION)) {
                        this.responseMessage = TelegramBotResponses.INPUT_DOCTOR_ID.getDescription();
                    } else {
                        Optional<PagebleDoctorDTO> doctors = doctorRequestService.getDoctors(PAGE_NUMBER, PAGE_SIZE);
                        this.responseMessage = doctors
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
                this.responseMessage = doctorByID.isPresent() ?
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
                        this.responseMessage = doctors
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
                        this.responseMessage = doctors
                                .get()
                                .toString();
                        sendMessage.setReplyMarkup(doctorsKeyboard.getDoctorsKeyboard(PAGE_NUMBER, MAX_PAGE_NUMBER));
                    }
                    case "STOP" -> {
                        chatStateRequestService.updateChatState(CHAT_ID, ChatStates.DEFAULT);
                        sendMessage.setReplyMarkup(null);
                        this.responseMessage = "Operation '%s' has stopped".formatted(chatState.getChatStates().getDescription());
                    }
                }
            }
        }

        sendMessage.setText(responseMessage);
        sendMessage.setChatId(CHAT_ID);

        return sendMessage;
    }

    @Override
    public Optional<ChatState> moveChatState(long chatID) {
        return chatStateRequestService.moveChatStateToNextState(chatID);
    }
}
