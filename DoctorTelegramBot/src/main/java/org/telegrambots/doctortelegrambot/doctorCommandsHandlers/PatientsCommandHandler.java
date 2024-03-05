package org.telegrambots.doctortelegrambot.doctorCommandsHandlers;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegrambots.doctortelegrambot.entities.Patient;
import org.telegrambots.doctortelegrambot.entities.TelegramBotResponses;
import org.telegrambots.doctortelegrambot.exceptions.RestTemplateExceptionHandler;

import java.util.Arrays;
import java.util.List;

@Component
public class PatientsCommandHandler implements Command {

    private final SendMessage sendMessage = new SendMessage();

    private final RestTemplate restTemplate = new RestTemplateBuilder().errorHandler(new RestTemplateExceptionHandler()).build();


    private String responseMessage = "";

    @Override
    public SendMessage sendResponse(Update update) {
        triggerMethodDependsOnCommand(update.getMessage().getText());
        sendMessage.setChatId(update.getMessage().getChatId());
        return sendMessage;
    }

    private void triggerMethodDependsOnCommand(String message) {
        var messageArray = message.split(" ");
        if (messageArray[0].equals("/patients")) {
            setResponseMessageForPatients(getPatients());
        } else {
            if (getPatientByID(extractPatientID(message)) != null) {
                setResponseMessageForPatient(getPatientByID(extractPatientID(message)));
            } else {
                clearResponseMessage();
                this.responseMessage = TelegramBotResponses.PATIENT_NOT_FOUNT.getDescription();
            }
        }
        this.sendMessage.setText(responseMessage);
    }

    private int extractPatientID(String message) {
        return message.split(" ").length == 2 ?
                Integer.parseInt(message.split(" ")[1]) :
                -1;
    }

    private Patient getPatientByID(int id) {
        ResponseEntity<Patient> optionalPatient = restTemplate.getForEntity("http://localhost:8080/api/v1/patient/{id}", Patient.class, id);
        return optionalPatient.getStatusCode().equals(HttpStatus.NOT_FOUND) ?
                null :
                optionalPatient.getBody();

    }

    private List<Patient> getPatients() {
        return Arrays
                .stream(restTemplate.getForEntity("http://localhost:8080/api/v1/patient", Patient[].class).getBody())
                .toList();
    }

    private void setResponseMessageForPatient(Patient patient) {
        clearResponseMessage();
        responseMessage += "id : [%s]\nPatient : [%s %s]\nDisease : [%s]\nState : [%s]\nChamber : [%s]\nDescription : [%s]"
                .formatted(patient.getId(), patient.getName(), patient.getSecondName(), patient.getDisease(), patient.getPatientState(), patient.getChamberNumber(), patient.getDescription());
    }

    private void setResponseMessageForPatients(List<Patient> patients) {
        clearResponseMessage();
        patients.stream().forEach(patient -> {
            responseMessage += "id : [%s]\nPatient : [%s %s]\nDisease : [%s]\nState : [%s]\nChamber : [%s]\nDescription : [%s]\n----------------------------------------------"
                    .formatted(patient.getId(), patient.getName(), patient.getSecondName(), patient.getDisease(), patient.getPatientState(), patient.getChamberNumber(), patient.getDescription());
        });
    }

    private void clearResponseMessage() {
        this.responseMessage = "";
    }
}
