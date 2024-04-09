package org.patientbot.patienttelegrambot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor

public class EmergencyRequestService {

    private final RestTemplate restTemplate;

    public boolean sendEmergencyHelpRequest(long chatID) {
        ResponseEntity<ResponseEntity> responseEntityResponseEntity = restTemplate.postForEntity("http://localhost:8086/emergency/api/help?chatID=%s"
                .formatted(chatID), null, ResponseEntity.class);
        return responseEntityResponseEntity.getStatusCode().is2xxSuccessful();
    }

}
