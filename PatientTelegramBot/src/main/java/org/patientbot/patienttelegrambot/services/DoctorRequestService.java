package org.patientbot.patienttelegrambot.services;

import lombok.RequiredArgsConstructor;
import org.patientbot.patienttelegrambot.dtos.ChatStateDTO;
import org.patientbot.patienttelegrambot.dtos.DoctorDTO;
import org.patientbot.patienttelegrambot.dtos.PagebleDoctorDTO;
import org.patientbot.patienttelegrambot.entity.ChatStates;
import org.patientbot.patienttelegrambot.exception.RestTemplateExceptionHandler;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.print.Doc;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class DoctorRequestService {

    private final RestTemplate restTemplate;

    public Optional<PagebleDoctorDTO> getDoctors(int pageNumber, int pageSize) {
        ResponseEntity<PagebleDoctorDTO> allDoctors = restTemplate.getForEntity("http://localhost:8084/hospital/api/doctors?pageNumber=%s&pageSize=%s"
                .formatted(pageNumber, pageSize), PagebleDoctorDTO.class);
        return allDoctors.getStatusCode().is2xxSuccessful() ?
                Optional.of(allDoctors.getBody()) :
                Optional.empty();
    }

    public Optional<DoctorDTO> getDoctorByID(long id) {
        ResponseEntity<DoctorDTO> optionalDoctor = restTemplate.getForEntity("http://localhost:8084/hospital/api/doctors/%s"
                .formatted(id), DoctorDTO.class);
        return optionalDoctor.getStatusCode().is2xxSuccessful() ?
                Optional.of(optionalDoctor.getBody()) :
                Optional.empty();
    }
}
