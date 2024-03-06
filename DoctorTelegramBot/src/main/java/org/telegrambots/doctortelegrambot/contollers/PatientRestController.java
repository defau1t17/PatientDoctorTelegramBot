package org.telegrambots.doctortelegrambot.contollers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegrambots.doctortelegrambot.dto.PatientDTO;
import org.telegrambots.doctortelegrambot.entities.Patient;
import org.telegrambots.doctortelegrambot.entities.Permission;
import org.telegrambots.doctortelegrambot.repositories.PermissionRepository;
import org.telegrambots.doctortelegrambot.services.PatientService;

@RestController
@RequestMapping("/api/v1/patient")
@RequiredArgsConstructor
public class PatientRestController {

    private final PatientService patientService;

    private final PermissionRepository permissionRepository;

    @GetMapping("/{id}")
    public ResponseEntity<?> getPatientByID(@PathVariable String id) {
        return patientService.findByID(Integer.parseInt(id)).isPresent() ?
                ResponseEntity.ok(patientService.findByID(Integer.parseInt(id)).get()) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping
    public ResponseEntity<?> getPatients() {
        return ResponseEntity.ok(patientService.findAll());
    }

    @PostMapping
    public ResponseEntity<?> createPatient(@RequestBody PatientDTO patientDTO) {
        if (patientService.validatePatientBeforeSave(patientDTO.convertDTOToPatient())) {
            Patient patient = patientDTO.convertDTOToPatient();
            patient.setPersonalToken(Permission.tokenFabric(permissionRepository));
            return ResponseEntity.status(HttpStatus.CREATED).body(patientService.create(patient));
        }
        return ResponseEntity.badRequest().build();
    }


}
