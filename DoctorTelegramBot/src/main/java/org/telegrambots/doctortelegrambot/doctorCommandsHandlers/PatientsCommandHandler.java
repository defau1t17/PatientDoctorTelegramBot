package org.telegrambots.doctortelegrambot.doctorCommandsHandlers;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegrambots.doctortelegrambot.entities.ChatState;
import org.telegrambots.doctortelegrambot.entities.ChatStates;
import org.telegrambots.doctortelegrambot.entities.Patient;
import org.telegrambots.doctortelegrambot.entities.TelegramBotResponses;
import org.telegrambots.doctortelegrambot.exceptions.RestTemplateExceptionHandler;
import org.telegrambots.doctortelegrambot.repositories.ChatStateRepository;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PatientsCommandHandler implements Command, StateUpdatable {

    private final SendMessage sendMessage = new SendMessage();
    private final RestTemplate restTemplate = new RestTemplateBuilder().errorHandler(new RestTemplateExceptionHandler()).build();

    private final ChatStateRepository chatStateRepository;

    private String responseMessage = "";

    private final String moveReference = "/patient";

    @Override
    public SendMessage sendResponse(Update update) {
        triggerMethodDependsOnCommand(update);
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText(responseMessage);
        return sendMessage;
    }

    private void triggerMethodDependsOnCommand(Update update) {
        ChatState chatState = chatStateRepository.findChatStateByChatID(Math.toIntExact(update.getMessage().getChatId())).orElse(null);
        var messageArray = update.getMessage().getText();
        if (messageArray.equals("/patients") && chatState != null && chatState.getChatStates().equals(ChatStates.DEFAULT)) {
            setResponseMessageForPatients(getPatients());
        } else if (messageArray.equals("/patient") && chatState != null) {
            responseOnState(chatState, update);
            StateUpdatable.updateState(chatStateRepository, chatState, moveReference);
        }
        else{
            this.responseMessage = "Unknown error happened";
        }
    }

    private Patient getPatientByID(int id) {
        ResponseEntity<Patient> optionalPatient = restTemplate.getForEntity("http://localhost:8080/api/v1/patient/{id}", Patient.class, id);
        return optionalPatient.getStatusCode().is4xxClientError() ?
                null :
                optionalPatient.getBody();

    }

    private List<Patient> getPatients() {
        return Arrays
                .stream(restTemplate.getForEntity("http://localhost:8080/api/v1/patient", Patient[].class).getBody())
                .toList();
    }

    private void setResponseMessageForPatient(Patient patient) {
        this.responseMessage += "id : [%s]\nPatient : [%s %s]\nDisease : [%s]\nState : [%s]\nChamber : [%s]\nDescription : [%s]"
                .formatted(patient.getId(), patient.getName(), patient.getSecondName(), patient.getDisease(), patient.getPatientState(), patient.getChamberNumber(), patient.getDescription());
    }

    private void setResponseMessageForPatients(List<Patient> patients) {
        patients.stream().forEach(patient -> {
            this.responseMessage += "id : [%s]\nPatient : [%s %s]\nDisease : [%s]\nState : [%s]\nChamber : [%s]\nDescription : [%s]\n----------------------------------------------"
                    .formatted(patient.getId(), patient.getName(), patient.getSecondName(), patient.getDisease(), patient.getPatientState(), patient.getChamberNumber(), patient.getDescription());
        });
    }


    @Override
    public void responseOnState(ChatState chatState, Update update) {
        String text = update.getMessage().getText();

        switch (chatState.getChatStates()) {
            case DEFAULT -> {
                this.responseMessage = "Please, enter ID of the patient below : ";
            }
            case WAITING_FOR_PATIENT_ID -> {
                Patient patientByID = getPatientByID(Integer.parseInt(text));
                if (patientByID != null) {
                    setResponseMessageForPatient(patientByID);
                } else {
                    this.responseMessage = TelegramBotResponses.PATIENT_NOT_FOUNT.getDescription();
                }
            }
            default -> {
                chatState.setChatStates(ChatStates.DEFAULT);
                chatState = chatStateRepository.save(chatState);
                responseOnState(chatState, update);
            }
        }
    }
}
