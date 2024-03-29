package org.telegrambots.doctortelegrambot.doctorCommandsHandlers;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegrambots.doctortelegrambot.dto.PaginatedPatientsResponse;
import org.telegrambots.doctortelegrambot.entities.ChatState;
import org.telegrambots.doctortelegrambot.entities.ChatStates;
import org.telegrambots.doctortelegrambot.entities.Patient;
import org.telegrambots.doctortelegrambot.exceptions.RestTemplateExceptionHandler;
import org.telegrambots.doctortelegrambot.repositories.ChatStateRepository;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class PatientsCommandHandler implements Command, StateUpdatable {

    private final SendMessage sendMessage = new SendMessage();
    private final RestTemplate restTemplate = new RestTemplateBuilder().errorHandler(new RestTemplateExceptionHandler()).build();

    private final PatientsKeyboard patientsKeyboard;

    private final ChatStateRepository chatStateRepository;

    private String responseMessage = "";

    private int PAGE_NUMBER = 0;
    private final int PAGE_SIZE = 1;

    private int MAX_PAGE_NUMBER = 0;

    private final String PATIENT_MOVE_REFERENCE = "/patient";
    private final String PATIENTS_MOVE_REFERENCE = "/patients";

    private ChatState chatState = null;

    private long chatID = -1;

    @Override
    public SendMessage sendResponse(Update update) {
        triggerBlock(update);
        sendMessage.setChatId(chatID);
        sendMessage.setText(responseMessage);
        return sendMessage;
    }

    private void triggerBlock(Update update) {
        if (update.hasMessage()) {
            chatID = update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            chatID = update.getCallbackQuery().getMessage().getChatId();
        }
        chatState = chatStateRepository.findChatStateByChatID(chatID).get();
        responseOnState(chatState, update);
    }

    private Patient getPatientByID(int id) {
        ResponseEntity<Patient> optionalPatient = restTemplate.getForEntity("http://localhost:8080/api/v1/patient/{id}", Patient.class, id);
        return optionalPatient.getStatusCode().is2xxSuccessful() ?
                optionalPatient.getBody() :
                null;
    }

    private List<Patient> getPatients() {
        ResponseEntity<PaginatedPatientsResponse<Patient>> patients = restTemplate.exchange("http://localhost:8080/api/v1/patient?page={pageNumber}&size={pageSize}", HttpMethod.GET, null, new ParameterizedTypeReference<PaginatedPatientsResponse<Patient>>() {
        }, Map.of("pageNumber", PAGE_NUMBER, "pageSize", PAGE_SIZE));
        MAX_PAGE_NUMBER = Objects.requireNonNull(patients.getBody()).getTotalPages() - 1;
        return patients.getBody().getContent();
    }

    private void setResponseMessageForPatient(Patient patient) {
        this.responseMessage += "\nid : [%s]\nPatient : [%s %s]\nDisease : [%s]\nState : [%s]\nChamber : [%s]\nDescription : [%s]"
                .formatted(patient.getId(), patient.getName(), patient.getSecondName(), patient.getDisease(), patient.getPatientState(), patient.getChamberNumber(), patient.getDescription());
    }

    private void setResponseMessageForPatients(List<Patient> patients) {
        this.responseMessage = "";
        patients.stream().forEach(patient -> {
            this.responseMessage += "\nid : [%s]\nPatient : [%s %s]\nDisease : [%s]\nState : [%s]\nChamber : [%s]\nDescription : [%s]\n----------------------------------------------"
                    .formatted(patient.getId(), patient.getName(), patient.getSecondName(), patient.getDisease(), patient.getPatientState(), patient.getChamberNumber(), patient.getDescription());
        });
    }

    @Override
    public void responseOnState(ChatState chatState, Update update) {
        switch (chatState.getChatStates()) {
            case DEFAULT -> {
                if (update.getMessage().getText().equals(PATIENT_MOVE_REFERENCE)) {
                    this.responseMessage = "Please, enter ID of the patient below : ";
                } else {
                    setResponseMessageForPatients(getPatients());
                    sendMessage.setReplyMarkup(patientsKeyboard.getPatientsKeyboard(PAGE_NUMBER, MAX_PAGE_NUMBER));
                }
                updateStateOnCommandOrState(update.getMessage().getText());
            }
            case WAITING_FOR_PATIENT_ID -> {
                Patient patientByID = getPatientByID(Integer.parseInt(update.getMessage().getText()));
                if (patientByID != null) {
                    setResponseMessageForPatient(patientByID);
                } else {
                    this.responseMessage = TelegramBotResponses.PATIENT_NOT_FOUNT.getDescription();
                }
            }
            case WAITING_FOR_PREVIOUS_OR_NEXT_COMMAND -> {
                String moveDirection = update.getCallbackQuery().getData();
                switch (moveDirection) {
                    case "NEXT" -> {
                        PAGE_NUMBER++;
                        setResponseMessageForPatients(getPatients());
                        sendMessage.setReplyMarkup(patientsKeyboard.getPatientsKeyboard(PAGE_NUMBER, MAX_PAGE_NUMBER));
                    }
                    case "BACK" -> {
                        PAGE_NUMBER--;
                        setResponseMessageForPatients(getPatients());
                        sendMessage.setReplyMarkup(patientsKeyboard.getPatientsKeyboard(PAGE_NUMBER, MAX_PAGE_NUMBER));
                    }
                    case "STOP" -> {
                        chatState.setChatStates(ChatStates.DEFAULT);
                        chatStateRepository.save(chatState);
                        sendMessage.setReplyMarkup(null);
                        responseMessage = "Stopped";
                    }
                }
            }
            default -> {
                chatState.setChatStates(ChatStates.DEFAULT);
                chatState = chatStateRepository.save(chatState);
                responseOnState(chatState, update);
            }
        }
    }

    private void updateStateOnCommandOrState(String resource) {
        if (chatState.getChatStates().equals(ChatStates.DEFAULT)) {
            StateUpdatable.updateState(chatStateRepository, chatState, resource.equals(PATIENT_MOVE_REFERENCE) ?
                    PATIENT_MOVE_REFERENCE :
                    PATIENTS_MOVE_REFERENCE);
        } else if (chatState.getChatStates().equals(ChatStates.WAITING_FOR_PATIENT_ID)) {
            StateUpdatable.updateState(chatStateRepository, chatState, chatState.getChatStates().getCommandReference());
        }
    }


}
