package org.telegrambots.doctortelegrambot.rabbitMQMessageConfigurations;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@EnableRabbit
public class DoctorConsumer {

    @RabbitListener(queues = "doctor")
    public void doctorConsumer(@Payload int chatID) {
        System.out.printf("chatid : %s%n", chatID);
    }
}
