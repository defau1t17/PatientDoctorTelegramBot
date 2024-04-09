package org.telegrambots.doctortelegrambot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegrambots.doctortelegrambot.dto.DoctorDTO;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShiftRequestService {

    private final RestTemplate restTemplate;

    public Optional<DoctorDTO> changeDoctorShiftStatus(long chatID) {
        ResponseEntity<DoctorDTO> optionalDoctor = restTemplate.postForEntity("http://localhost:8084/hospital/api/doctors/%s/shift"
                .formatted(chatID), null, DoctorDTO.class);
        return optionalDoctor.getStatusCode().is2xxSuccessful() ?
                Optional.of(optionalDoctor.getBody()) :
                Optional.empty();
    }

}
