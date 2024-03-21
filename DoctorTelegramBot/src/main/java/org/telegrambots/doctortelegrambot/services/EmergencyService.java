package org.telegrambots.doctortelegrambot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegrambots.doctortelegrambot.entities.Patient;

import static java.util.Arrays.*;

@Service
@EnableRabbit
@RequiredArgsConstructor
public class EmergencyService {

    private final RabbitTemplate template;

    public void emergencyCallerByPatientState(Patient patient) {
        switch (patient.getPatientState()) {

            case STABLE -> {
                callForObject(patient.getPersonalToken().getChatID(), "nurse");
            }
            case HARD -> {
                callForObject(patient.getPersonalToken().getChatID(), "nurse", "doctor");
            }
            case CRITICAL -> {
                callForObject(patient.getPersonalToken().getChatID(), "nurse", "doctor", "paramedic");
            }
        }
    }

    private void callForObject(long patientChatID, String... doctors) {
        stream(doctors)
                .forEach(doctor ->
                        template.convertAndSend(doctor, patientChatID));
    }

}
