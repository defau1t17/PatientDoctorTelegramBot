package org.patientbot.patienttelegrambot.configurations;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:application.properties")
@Data
public class PatientTelegramBotConfiguration {
    @Value("${bot.patientbot.name}")
    private String botName;

    @Value("${bot.patientbot.token}")
    private String token;
}
