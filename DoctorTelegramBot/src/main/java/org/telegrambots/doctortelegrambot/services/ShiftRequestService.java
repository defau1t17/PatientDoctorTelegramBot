package org.telegrambots.doctortelegrambot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegrambots.doctortelegrambot.dto.DoctorDTO;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@PropertySource(value = "classpath:application.properties")
public class ShiftRequestService {

    @Value(value = "${hospital.service.address}")
    private String hospitalService;
    private final RestTemplate restTemplate;

    public Optional<DoctorDTO> changeDoctorShiftStatus(long chatID) {
        ResponseEntity<DoctorDTO> optionalDoctor = restTemplate.postForEntity("http://%s:8084/hospital/api/doctors/%s/shift"
                .formatted(hospitalService, chatID), null, DoctorDTO.class);
        return optionalDoctor.getStatusCode().is2xxSuccessful() ?
                Optional.of(optionalDoctor.getBody()) :
                Optional.empty();
    }

}
