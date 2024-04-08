package org.telegrambots.doctortelegrambot.services;

import lombok.RequiredArgsConstructor;
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
public class PatientRequestService {

    private final RestTemplate restTemplate;

    public Optional<PatientDTO> createNewPatient(NewPatientDTO newPatientDTO) {
        ResponseEntity<PatientDTO> optionalNewPatient = restTemplate.postForEntity("http://localhost:8084/hospital/api/patients", newPatientDTO, org.telegrambots.doctortelegrambot.dto.PatientDTO.class);
        return optionalNewPatient.getStatusCode().is2xxSuccessful() ?
                Optional.of(optionalNewPatient.getBody()) :
                Optional.empty();
    }

    public PaginatedPatientsDTO getPaginatedPatients(int pageNumber, int pageSize) {
        ResponseEntity<PaginatedPatientsDTO> paginatedPatients = restTemplate.getForEntity("http://localhost:8084/hospital/api/patients?pageNumber=%s&pageSize=%s"
                .formatted(pageNumber, pageSize), PaginatedPatientsDTO.class);
        return paginatedPatients.getStatusCode().is2xxSuccessful() ?
                paginatedPatients.getBody() :
                null;
    }

    public Optional<PatientDTO> getPatientByID(long id) {
        ResponseEntity<PatientDTO> optionalPatient = restTemplate.getForEntity("http://localhost:8084/hospital/api/patients/%s"
                .formatted(id), PatientDTO.class);
        return optionalPatient.getStatusCode().is2xxSuccessful() ?
                Optional.of(optionalPatient.getBody()) :
                Optional.empty();
    }

    public String createPatientToken() {
        return restTemplate.getForEntity("http://localhost:8082/chat/api/token", String.class).getBody();
    }

}
