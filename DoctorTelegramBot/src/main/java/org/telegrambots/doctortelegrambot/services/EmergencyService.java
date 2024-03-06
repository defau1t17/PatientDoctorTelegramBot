package org.telegrambots.doctortelegrambot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegrambots.doctortelegrambot.entities.Patient;

@Service
@EnableRabbit
public class EmergencyService {

    @Autowired
    private RabbitTemplate template;

    public void emergencyCallerByPatientState(Patient patient) {
        switch (patient.getPatientState()) {
            case Stable -> {
                callForNurse(patient.getPersonalToken().getChatID());
            }
            case Hard -> {
                callForNurse(patient.getPersonalToken().getChatID());
                callForDoctor(patient.getPersonalToken().getChatID());
            }
            case Critical -> {
                callForNurse(patient.getPersonalToken().getChatID());
                callForDoctor(patient.getPersonalToken().getChatID());
                callForParamedic(patient.getPersonalToken().getChatID());
            }
            default -> {
            }
        }
    }

    private void callForNurse(int patientChatID) {
        template.convertAndSend("nurse", patientChatID);
    }

    private void callForDoctor(int patientChatID) {
        template.convertAndSend("doctor", patientChatID);
    }

    private void callForParamedic(int patientChatID) {
        template.convertAndSend("paramedic", patientChatID);
    }


}
