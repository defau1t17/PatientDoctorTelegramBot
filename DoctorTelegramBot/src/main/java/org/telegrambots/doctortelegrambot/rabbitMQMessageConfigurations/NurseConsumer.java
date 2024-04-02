package org.telegrambots.doctortelegrambot.rabbitMQMessageConfigurations;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.telegrambots.doctortelegrambot.dto.EmergencyDTO;

@Component
public class NurseConsumer {

    @RabbitListener(queues = "nurse")
    public void sendMessageToNurse(EmergencyDTO emergencyDTO) {
        System.out.println(emergencyDTO.toString());
    }
}
