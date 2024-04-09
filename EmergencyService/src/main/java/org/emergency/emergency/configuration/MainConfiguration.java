package org.emergency.emergency.configuration;

import org.emergency.emergency.exception.RestTemplateExceptionHandler;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class MainConfiguration {

    @Bean
    public RestTemplate createRestTemplate() {
        return new RestTemplateBuilder()
                .errorHandler(new RestTemplateExceptionHandler())
                .build();
    }
}
