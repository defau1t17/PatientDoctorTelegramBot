package org.patientbot.patienttelegrambot.configurations;

import org.patientbot.patienttelegrambot.exception.RestTemplateExceptionHandler;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class MainConfiguration {

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplateBuilder()
                .errorHandler(new RestTemplateExceptionHandler())
                .build();
    }
}
