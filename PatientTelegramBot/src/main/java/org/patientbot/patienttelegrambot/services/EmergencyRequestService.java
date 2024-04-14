package org.patientbot.patienttelegrambot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@PropertySource(value = "classpath:application.properties")
public class EmergencyRequestService {

    @Value(value = "${emergency.service.address}")
    private String emergencyAddress;
    private final RestTemplate restTemplate;

    public boolean sendEmergencyHelpRequest(long chatID) {
        ResponseEntity<ResponseEntity> responseEntityResponseEntity = restTemplate.postForEntity("http://%s:8086/emergency/api/help?chatID=%s"
                .formatted(emergencyAddress, chatID), null, ResponseEntity.class);
        return responseEntityResponseEntity.getStatusCode().is2xxSuccessful();
    }

}
