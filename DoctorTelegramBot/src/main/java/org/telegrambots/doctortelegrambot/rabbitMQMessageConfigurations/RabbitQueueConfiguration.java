package org.telegrambots.doctortelegrambot.rabbitMQMessageConfigurations;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitQueueConfiguration {

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue nurseQueue() {
        return new Queue("nurse");
    }

    @Bean

    public Queue paramedicQueue() {
        return new Queue("paramedic");
    }

    @Bean

    public Queue doctorQueue() {
        return new Queue("doctor");
    }

}
