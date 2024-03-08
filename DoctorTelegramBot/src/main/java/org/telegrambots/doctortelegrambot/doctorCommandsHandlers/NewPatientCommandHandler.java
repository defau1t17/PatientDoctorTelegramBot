package org.telegrambots.doctortelegrambot.doctorCommandsHandlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegrambots.doctortelegrambot.entities.ChatState;
import org.telegrambots.doctortelegrambot.entities.Patient;
import org.telegrambots.doctortelegrambot.entities.PatientState;
import org.telegrambots.doctortelegrambot.entities.Permission;
import org.telegrambots.doctortelegrambot.repositories.ChatStateRepository;
import org.telegrambots.doctortelegrambot.repositories.PermissionRepository;
import org.telegrambots.doctortelegrambot.services.PatientService;


@Component
public class NewPatientCommandHandler implements Command {
    private SendMessage sendMessage = new SendMessage();

    private Patient newPatient = new Patient();

    @Autowired
    private ChatStateRepository chatStateRepository;

    @Autowired
    private PatientService patientService;

    @Autowired
    private PermissionRepository permissionRepository;
    private String responseMessage = "";


    @Override
    public SendMessage sendResponse(Update update) {
        ChatState chatState = chatStateRepository.findChatStateByChatID(Math.toIntExact(update.getMessage().getChatId())).get();
        buildPatient(chatState, update);
        sendMessage.setText(responseMessage);
        sendMessage.setChatId(update.getMessage().getChatId());
        return sendMessage;
    }

    private void buildPatient(ChatState chatState, Update update) {
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
                this.responseMessage = "Please input the chamber number : ";
            }
            case WAITING_FOR_CHAMBER_NUMBER -> {
                this.newPatient.setChamberNumber(Integer.parseInt(message));
                this.responseMessage = "You may add some description(if not just print '-' : ";
            }
            case WAITING_FOR_DESCRIPTION -> {
                this.newPatient.setDescription(message);
                this.newPatient.setPersonalToken(Permission.tokenFabric(permissionRepository));
                newPatient = patientService.create(newPatient);
                if (patientService.validatePatientBeforeSave(newPatient)) {
                    this.responseMessage = "New patient created : \nID :[%s]\nToken : [%s]\nName : [%s]\nSecond name : [%s]\nDisease : [%s]\nState : [%s]\nChamber : [%s]\nDescription : [%s]"
                            .formatted(
                                    this.newPatient.getId(),
                                    this.newPatient.getPersonalToken().getPermissionToken(),
                                    this.newPatient.getName(),
                                    this.newPatient.getSecondName(),
                                    this.newPatient.getDisease(),
                                    this.newPatient.getPatientState(),
                                    this.newPatient.getChamberNumber(),
                                    this.newPatient.getDescription());
                    clearNewPatient();
                } else {
                    this.responseMessage = "An error happened while validating a new patient!\n Unfortunately we can't save this patient";
                }
            }
        }
        updateState(chatState);
    }

    // create ability to modify patient data by telegram bot buttons

    private void updateState(ChatState chatState) {
        chatState.setChatStates(chatState.getChatStates().next());
        chatStateRepository.save(chatState);
    }

    private void clearNewPatient() {
        this.newPatient = null;
    }


}
