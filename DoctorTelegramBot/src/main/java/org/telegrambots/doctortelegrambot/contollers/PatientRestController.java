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

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/patient")
@RequiredArgsConstructor
public class PatientRestController {

    private final PatientService patientService;

    private final PermissionRepository permissionRepository;

    @GetMapping("/{id}")
    public ResponseEntity<?> getPatientByID(@PathVariable int id) {
        return patientService.findByID(id).isPresent() ?
                ResponseEntity
                        .ok(patientService.findByID(id).get()) :
                ResponseEntity
                        .notFound()
                        .build();
    }

    @GetMapping
    public ResponseEntity<?> getPatients(@RequestParam(name = "page") Optional<Integer> page, @RequestParam(name = "size") Optional<Integer> size) {
        return ResponseEntity
                .ok(patientService.findAllByPage(page.orElse(0), size.orElse(5)));
    }

    @PostMapping
    public ResponseEntity<?> createPatient(@RequestBody PatientDTO patientDTO) {
        System.out.println(patientDTO);
        if (patientService.validatePatientBeforeSave(patientDTO.convertDTOToPatient())) {
            Patient patient = patientDTO.convertDTOToPatient();
            patient.setPersonalToken(Permission.tokenFabric(permissionRepository));
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(patientService.create(patient));
        } else return
                ResponseEntity
                        .badRequest()
                        .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePatientByID(@PathVariable(name = "id") String id) {
        Optional<Patient> optionalPatient = patientService.findByID(Integer.valueOf(id));
        if (optionalPatient.isPresent()) {
            patientService.delete(optionalPatient.get());
            return ResponseEntity
                    .ok()
                    .build();
        } else
            return ResponseEntity
                    .notFound()
                    .build();
    }


}
