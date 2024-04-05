package org.telegrambots.doctortelegrambot.rabbitMQMessageConfigurations;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.telegrambots.doctortelegrambot.dto.EmergencyDTO;

@Component
@EnableRabbit
public class DoctorConsumer {

    @RabbitListener(queues = "doctor")
    public void doctorConsumer(EmergencyDTO emergencyDTO) {
        System.out.printf("chatid : %s%n", emergencyDTO.toString());
    }
}
