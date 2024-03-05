package org.telegrambots.doctortelegrambot.doctorBotConfigurations;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:application.properties")
@Data
public class DoctorTelegramBotConfiguration {
    @Value("${bot.doctorbot.name}")
    private String botName;

    @Value("${bot.doctorbot.token}")
    private String token;


}
