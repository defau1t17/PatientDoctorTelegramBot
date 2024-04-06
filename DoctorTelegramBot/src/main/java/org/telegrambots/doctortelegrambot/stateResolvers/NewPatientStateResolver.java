package org.telegrambots.doctortelegrambot.stateResolvers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegrambots.doctortelegrambot.commandKeyboards.NewPatientKeyboard;
import org.telegrambots.doctortelegrambot.dto.NewPatientDTO;
import org.telegrambots.doctortelegrambot.entities.ChatState;
import org.telegrambots.doctortelegrambot.entities.ChatStates;
import org.telegrambots.doctortelegrambot.entities.PatientState;
import org.telegrambots.doctortelegrambot.entities.TelegramBotResponses;
import org.telegrambots.doctortelegrambot.services.ChatStateRequestService;
import org.telegrambots.doctortelegrambot.services.PatientRequestService;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class NewPatientStateResolver implements ChatStateMovable {
    private final SendMessage sendMessage = new SendMessage();
    private final PatientRequestService requestService;
    private final ChatStateRequestService chatStateRequestService;
    private final NewPatientKeyboard newPatientKeyboard;
    private NewPatientDTO newPatientDTO = new NewPatientDTO();
    private String responseMessage = "";
    private long CHAT_ID = 0;

    @Override
    public SendMessage responseOnState(ChatState chatState, Update update) {

        if (update.hasMessage()) {
            CHAT_ID = update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            CHAT_ID = update.getCallbackQuery().getMessage().getChatId();
        }

        switch (chatState.getChatStates()) {
            case DEFAULT -> {
                this.responseMessage = "Please write patient name below : ";
                chatStateRequestService.updateChatState(CHAT_ID, ChatStates.WAITING_FOR_NAME);
            }
            case WAITING_FOR_NAME -> {
                this.newPatientDTO.setName(update.getMessage().getText());
                this.responseMessage = "Please write patient second name below : ";
                moveChatState(CHAT_ID);
            }
            case WAITING_FOR_SECONDNAME -> {
                this.newPatientDTO.setSecondName(update.getMessage().getText());
                this.responseMessage = "Please describe patient disease : ";
                moveChatState(CHAT_ID);

            }
            case WAITING_FOR_DISEASE -> {
                this.newPatientDTO.setDisease(update.getMessage().getText());
                sendMessage.setReplyMarkup(newPatientKeyboard.getStatesKeyboard());
                this.responseMessage = "Please choose one of the states : ";
                moveChatState(CHAT_ID);
            }
            case WAITING_FOR_PATIENT_STATE -> {
                try {
                    this.newPatientDTO.setPatientState(PatientState.valueOf(update.getCallbackQuery().getData()));
                    this.responseMessage = "Please input the chamber number in range [10-999] : ";
                    moveChatState(CHAT_ID);
                    sendMessage.setReplyMarkup(null);
                } catch (Exception e) {
                    this.responseMessage = TelegramBotResponses.SYNTAX_ERROR.getDescription();
                    responseOnState(chatState, update);
                }
            }
            case WAITING_FOR_CHAMBER_NUMBER -> {
                try {
                    int chamberNumber = Integer.parseInt(update.getMessage().getText());
                    if (chamberNumber < 0 || chamberNumber > 100) throw new Exception();
                    else {
                        this.newPatientDTO.setChamberNumber(chamberNumber);
                        this.responseMessage = "You may add some description(if not just print '-') : ";
                        moveChatState(CHAT_ID);
                    }
                } catch (Exception e) {
                    this.responseMessage = TelegramBotResponses.SYNTAX_ERROR.getDescription()
                            .concat("Please input the chamber number in range [10-999] : ");
                    responseOnState(chatState, update);
                }
            }
            case WAITING_FOR_DESCRIPTION -> {
                this.newPatientDTO.setDescription(update.getMessage().getText());
                this.newPatientDTO.setToken(requestService.createPatientToken());
                this.responseMessage = "New patient to be created : \n%s".formatted(newPatientDTO.toString());
                sendMessage.setReplyMarkup(newPatientKeyboard.getApproveKeyboard());
                moveChatState(CHAT_ID);
            }
            case WAITING_FOF_NAME_UPDATE -> {
                this.newPatientDTO.setName(update.getMessage().getText());
                updateKeyboardAndResponseMessageAfterUpdate();
            }
            case WAITING_FOF_SECONDNAME_UPDATE -> {
                this.newPatientDTO.setSecondName(update.getMessage().getText());
                updateKeyboardAndResponseMessageAfterUpdate();
            }
            case WAITING_FOF_DISEASE_UPDATE -> {
                this.newPatientDTO.setDisease(update.getMessage().getText());
                updateKeyboardAndResponseMessageAfterUpdate();
            }
            case WAITING_FOF_STATE_UPDATE -> {
                try {
                    this.newPatientDTO.setPatientState(PatientState.valueOf(update.getCallbackQuery().getData()));
                    updateKeyboardAndResponseMessageAfterUpdate();
                } catch (Exception e) {
                    chatStateRequestService.updateChatState(CHAT_ID, ChatStates.WAITING_FOF_STATE_UPDATE);
                    this.responseMessage = "Please select new patient state : ";
                    sendMessage.setReplyMarkup(newPatientKeyboard.getStatesKeyboard());
                }
            }
            case WAITING_FOF_CHAMBER_UPDATE -> {
                try {
                    int chamberNumber = Integer.parseInt(update.getMessage().getText());
                    if (chamberNumber < 0 || chamberNumber > 100) throw new Exception();
                    else {
                        this.newPatientDTO.setChamberNumber(chamberNumber);
                        updateKeyboardAndResponseMessageAfterUpdate();
                    }
                } catch (Exception e) {
                    chatStateRequestService.updateChatState(CHAT_ID, ChatStates.WAITING_FOF_CHAMBER_UPDATE);
                    this.responseMessage = "The number you wrote is incorrect!\nPlease input new chamber number in range [10-999] : ";
                }
            }
            case WAITING_FOF_DESCRIPTION_UPDATE -> {
                this.newPatientDTO.setDescription(update.getMessage().getText());
                updateKeyboardAndResponseMessageAfterUpdate();
            }
            case WAITING_FOF_APPROVE -> {
                String callback = update.getCallbackQuery().getData();
                switch (callback) {
                    case "EDIT_NAME" -> {
                        chatStateRequestService.updateChatState(CHAT_ID, ChatStates.WAITING_FOF_NAME_UPDATE);
                        this.responseMessage = "Please input new name below: ";
                        sendMessage.setReplyMarkup(null);

                    }
                    case "EDIT_SECOND_NAME" -> {
                        chatStateRequestService.updateChatState(CHAT_ID, ChatStates.WAITING_FOF_SECONDNAME_UPDATE);
                        this.responseMessage = "Please input new second name below: ";
                        sendMessage.setReplyMarkup(null);

                    }
                    case "EDIT_DISEASE" -> {
                        chatStateRequestService.updateChatState(CHAT_ID, ChatStates.WAITING_FOF_DISEASE_UPDATE);
                        this.responseMessage = "Please input new disease  below: ";
                        sendMessage.setReplyMarkup(null);
                    }

                    case "EDIT_STATE" -> {
                        chatStateRequestService.updateChatState(CHAT_ID, ChatStates.WAITING_FOF_STATE_UPDATE);
                        this.responseMessage = "Please select new patient state : ";
                        sendMessage.setReplyMarkup(newPatientKeyboard.getStatesKeyboard());
                        sendMessage.setReplyMarkup(null);
                    }
                    case "EDIT_CHAMBER" -> {
                        chatStateRequestService.updateChatState(CHAT_ID, ChatStates.WAITING_FOF_CHAMBER_UPDATE);
                        this.responseMessage = "Please input new chamber number in range [10-999]: ";
                        sendMessage.setReplyMarkup(null);
                    }
                    case "EDIT_DESCRIPTION" -> {
                        chatStateRequestService.updateChatState(CHAT_ID, ChatStates.WAITING_FOF_DESCRIPTION_UPDATE);
                        this.responseMessage = "Please input new description below : ";
                        sendMessage.setReplyMarkup(null);
                    }
                    case "SAVE" -> {
                        requestService.createNewPatient(newPatientDTO);
                        this.responseMessage = "New patient successfully saved";
                        newPatientDTO = new NewPatientDTO();
                        sendMessage.setReplyMarkup(null);
                        chatStateRequestService.rollBackChatStateToDefault(CHAT_ID);
                    }
                    case "CLEAR" -> {
                        newPatientDTO = new NewPatientDTO();
                        this.responseMessage = "New patient successfully removed";
                        chatStateRequestService.rollBackChatStateToDefault(CHAT_ID);
                        sendMessage.setReplyMarkup(null);
                        chatStateRequestService.rollBackChatStateToDefault(CHAT_ID);
                    }
                }
            }
            default -> {
                Optional<ChatState> optionalChatState = chatStateRequestService.rollBackChatStateToDefault(CHAT_ID);
                if (optionalChatState.isPresent()) {
                    responseOnState(chatState, update);
                } else {
                    this.responseMessage = TelegramBotResponses.SOME_ERROR.getDescription();
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

    private void updateKeyboardAndResponseMessageAfterUpdate() {
        this.responseMessage = "New patient to be created : \n%s"
                .formatted(newPatientDTO.toString());
        sendMessage.setReplyMarkup(newPatientKeyboard.getApproveKeyboard());
        chatStateRequestService.updateChatState(CHAT_ID, ChatStates.WAITING_FOF_APPROVE);
    }
}
