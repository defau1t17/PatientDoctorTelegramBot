package org.telegrambots.doctortelegrambot.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NurseConsumer {

    @RabbitListener(queues = "nurse")
    public void sendMessageToNurse() {

    }
}
