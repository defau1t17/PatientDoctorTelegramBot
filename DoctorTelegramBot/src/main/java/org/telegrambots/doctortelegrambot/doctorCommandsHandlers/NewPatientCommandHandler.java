package org.telegrambots.doctortelegrambot.doctorCommandsHandlers;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegrambots.doctortelegrambot.dto.PatientDTO;
import org.telegrambots.doctortelegrambot.entities.ChatState;
import org.telegrambots.doctortelegrambot.entities.ChatStates;
import org.telegrambots.doctortelegrambot.entities.Patient;
import org.telegrambots.doctortelegrambot.entities.PatientState;
import org.telegrambots.doctortelegrambot.exceptions.RestTemplateExceptionHandler;
import org.telegrambots.doctortelegrambot.repositories.ChatStateRepository;


@Component
@RequiredArgsConstructor
public class NewPatientCommandHandler implements Command, StateUpdatable {
    private SendMessage sendMessage = new SendMessage();

    private Patient newPatient = new Patient();

    private final ChatStateRepository chatStateRepository;

    private final RestTemplate restTemplate = new RestTemplateBuilder().errorHandler(new RestTemplateExceptionHandler()).build();

    private String responseMessage = "";

    private final String moveReference = "/new_patient";


    @Override
    public SendMessage sendResponse(Update update) {
        ChatState chatState = chatStateRepository.findChatStateByChatID(update.getMessage().getChatId()).orElse(null);
        if (chatState == null) {
            this.responseMessage = "Unknown error happened";
        } else {
            responseOnState(chatState, update);
            StateUpdatable.updateState(chatStateRepository, chatState, moveReference);
        }
        sendMessage.setText(responseMessage);
        sendMessage.setChatId(update.getMessage().getChatId());
        return sendMessage;
    }

    @Override
    public void responseOnState(ChatState chatState, Update update) {
        String message = update.getMessage().getText();
        switch (chatState.getChatStates()) {
            case DEFAULT -> {
                this.responseMessage = "Please write patient name below : ";
            }
            case WAITING_FOR_NAME -> {
                this.newPatient.setName(message);
                this.responseMessage = "Please write patient second name below : ";
            }
            case WAITING_FOR_SECONDNAME -> {
                this.newPatient.setSecondName(message);
                this.responseMessage = "Please describe patient disease : ";
            }
            case WAITING_FOR_DISEASE -> {
                this.newPatient.setDisease(message);
                this.responseMessage = "Please input one of the states [STABLE, HARD, CRITICAL] : ";
            }
            case WAITING_FOR_PATIENT_STATE -> {
                this.newPatient.setPatientState(PatientState.valueOf(message.toUpperCase()));
                this.responseMessage = "Please input the chamber number in range [10-999] : ";
            }
            case WAITING_FOR_CHAMBER_NUMBER -> {
                this.newPatient.setChamberNumber(Integer.parseInt(message));
                this.responseMessage = "You may add some description(if not just print '-') : ";
            }
            case WAITING_FOR_DESCRIPTION -> {
                this.newPatient.setId(0);
                this.newPatient.setDescription(message);
                Patient optionalPatient = sendRequestForNewPatient();
                if (optionalPatient != null) {
                    this.responseMessage = "New patient to be created : " + optionalPatient.toString();
                } else {
                    this.responseMessage = "An error happened while validating a new patient!\n Unfortunately we can't save this patient";
                }
//                clearNewPatient();
                newPatient = new Patient();
            }
            default -> {
                chatState.setChatStates(ChatStates.DEFAULT);
                chatState = chatStateRepository.save(chatState);
                responseOnState(chatState, update);
            }
        }
    }

    // create ability to modify patient data by telegram bot buttons

    private void clearNewPatient() {
        this.newPatient = null;
    }

    private Patient sendRequestForNewPatient() {
        PatientDTO patientDTO = PatientDTO.covertPatientToDTO(newPatient);
        ResponseEntity<Patient> patientResponseEntity = restTemplate.postForEntity("http://localhost:8080/api/v1/patient", patientDTO, Patient.class, "");
        return patientResponseEntity.getStatusCode().is2xxSuccessful() ?
                patientResponseEntity.getBody() :
                null;
    }
}
