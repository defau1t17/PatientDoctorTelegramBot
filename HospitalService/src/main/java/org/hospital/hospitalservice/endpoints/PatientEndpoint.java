package org.hospital.hospitalservice.endpoints;

import lombok.RequiredArgsConstructor;
import org.hospital.hospitalservice.dtos.PatientDTO;
import org.hospital.hospitalservice.entities.Patient;
import org.hospital.hospitalservice.entities.User;
import org.hospital.hospitalservice.services.PatientService;
import org.hospital.hospitalservice.services.UserService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientEndpoint {

    private final PatientService patientService;

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getPatientByID(@PathVariable(value = "id") long id) {
        Optional<Patient> optionalPatient = patientService.findByID(id);
        return optionalPatient.isPresent() ?
                ResponseEntity
                        .ok(optionalPatient.get()) :
                ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .build();
    }
    @GetMapping("/chat/{chatID}")
    public ResponseEntity<?> getPatientByChatID(@PathVariable(value = "chatID") long chatID) {
        Optional<Patient> optionalPatient = patientService.findByChatID(chatID);
        return optionalPatient.isPresent() ?
                ResponseEntity
                        .ok(optionalPatient.get()) :
                ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .build();
    }

    @GetMapping
    public ResponseEntity<?> getPatients(@RequestParam(value = "page") Optional<Integer> page, @RequestParam(value = "size") Optional<Integer> size) {
        return ResponseEntity
                .ok(patientService.findAll(page.orElse(0), size.orElse(5)));
    }

    @PostMapping
    public ResponseEntity<?> createPatient(@RequestBody PatientDTO patientDTO) {
        if (patientService.validateBeforeSave(patientDTO.convertDTOToPatient())) {
            Patient patient = patientDTO.convertDTOToPatient();
            return ResponseEntity
                    .ok(patientService.create(patient));
        } else return
                ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .build();
    }

    @PatchMapping
    public ResponseEntity<?> updatePatientChatID(@RequestParam long chatID, @RequestParam String token) {
        Optional<User> optionalUser = userService.findUserByToken(token);
        return optionalUser.isPresent() ?
                ResponseEntity
                        .ok(userService.updateUserChatID(optionalUser.get(), chatID)) :
                ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePatientByID(@PathVariable(value = "id") long id) {
        Optional<Patient> optionalPatient = patientService.findByID(id);
        if (optionalPatient.isPresent()) {
            patientService.delete(optionalPatient.get());
            return ResponseEntity
                    .ok("deleted successfully");
        } else
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
    }

    @DeleteMapping("/chat/{chatID}")
    public ResponseEntity<?> deletePatientByChatID(@PathVariable(value = "chatID") long chatID) {
        Optional<Patient> optionalPatient = patientService.findByChatID(chatID);
        if (optionalPatient.isPresent()) {
            patientService.deleteByChatID(chatID);
            return ResponseEntity
                    .ok("deleted successfully");
        } else
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
    }

}
