package org.telegrambots.doctortelegrambot.rabbitMQMessageConfigurations;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class NurseConsumer {

    @RabbitListener(queues = "nurse")
    public void sendMessageToNurse(@Payload int patientChatID) {
        System.out.println(patientChatID);
    }
}
