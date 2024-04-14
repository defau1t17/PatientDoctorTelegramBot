package org.telegrambots.doctortelegrambot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegrambots.doctortelegrambot.dto.NewPatientDTO;
import org.telegrambots.doctortelegrambot.dto.PaginatedPatientsDTO;
import org.telegrambots.doctortelegrambot.dto.PatientDTO;
import org.telegrambots.doctortelegrambot.entities.ChatState;
import org.telegrambots.doctortelegrambot.entities.ChatStates;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:application.properties")
public class PatientRequestService {
    @Value(value = "${hospital.service.address}")
    private String hospitalService;
    @Value(value = "${chat.service.address}")
    private String chatService;
    private final RestTemplate restTemplate;

    public Optional<PatientDTO> createNewPatient(NewPatientDTO newPatientDTO) {
        ResponseEntity<PatientDTO> optionalNewPatient = restTemplate.postForEntity("http://%s:8084/hospital/api/patients"
                .formatted(hospitalService), newPatientDTO, org.telegrambots.doctortelegrambot.dto.PatientDTO.class);
        return optionalNewPatient.getStatusCode().is2xxSuccessful() ?
                Optional.of(optionalNewPatient.getBody()) :
                Optional.empty();
    }

    public PaginatedPatientsDTO getPaginatedPatients(int pageNumber, int pageSize) {
        ResponseEntity<PaginatedPatientsDTO> paginatedPatients = restTemplate.getForEntity("http://%s:8084/hospital/api/patients?pageNumber=%s&pageSize=%s"
                .formatted(hospitalService, pageNumber, pageSize), PaginatedPatientsDTO.class);
        return paginatedPatients.getStatusCode().is2xxSuccessful() ?
                paginatedPatients.getBody() :
                null;
    }

    public Optional<PatientDTO> getPatientByID(long id) {
        ResponseEntity<PatientDTO> optionalPatient = restTemplate.getForEntity("http://%s:8084/hospital/api/patients/%s"
                .formatted(hospitalService, id), PatientDTO.class);
        return optionalPatient.getStatusCode().is2xxSuccessful() ?
                Optional.of(optionalPatient.getBody()) :
                Optional.empty();
    }

    public String createPatientToken() {
        return restTemplate.getForEntity("http://%s:8082/chat/api/token"
                .formatted(chatService), String.class).getBody();
    }

}
