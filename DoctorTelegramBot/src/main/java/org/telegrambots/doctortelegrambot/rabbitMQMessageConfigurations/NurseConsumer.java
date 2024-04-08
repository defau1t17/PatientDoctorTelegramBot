package org.telegrambots.doctortelegrambot.rabbitMQMessageConfigurations;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegrambots.doctortelegrambot.controllers.DoctorTelegramBotController;
import org.telegrambots.doctortelegrambot.dto.EmergencyDTO;

@Component
@EnableRabbit
@RequiredArgsConstructor
public class NurseConsumer {

    private final DoctorTelegramBotController controller;


    @RabbitListener(queues = "nurse")
    public void sendMessageToNurse(EmergencyDTO emergencyDTO) throws TelegramApiException {
        controller.sendMessageToDoctor(emergencyDTO);
    }
}
