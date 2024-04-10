package org.emergency.emergency.service;

import lombok.RequiredArgsConstructor;
import org.emergency.emergency.dto.DoctorDTO;
import org.emergency.emergency.dto.PatientDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RestTemplate restTemplate;

    public Optional<PatientDTO> getPatientByChatID(long chatID) {
        ResponseEntity<PatientDTO> optionalPatient = restTemplate.getForEntity("http://localhost:8084/hospital/api/patients/chat/%s" .formatted(chatID), PatientDTO.class);
        return optionalPatient.getStatusCode().is2xxSuccessful() ?
                Optional.of(optionalPatient.getBody()) :
                Optional.empty();
    }

    public Optional<List<DoctorDTO>> getDoctorsByPatientID(int id) {
        ResponseEntity<DoctorDTO[]> optionalDoctorList = restTemplate.getForEntity("http://localhost:8084/hospital/api/doctors/patient/%s" .formatted(id), DoctorDTO[].class);
        return optionalDoctorList.getStatusCode().is2xxSuccessful() ?
                Optional.of(Arrays.stream(optionalDoctorList.getBody()).toList()) :
                Optional.empty();
    }


}
