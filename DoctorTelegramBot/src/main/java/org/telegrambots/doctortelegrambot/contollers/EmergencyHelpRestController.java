package org.telegrambots.doctortelegrambot.contollers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.telegrambots.doctortelegrambot.entities.Patient;
import org.telegrambots.doctortelegrambot.services.DoctorService;
import org.telegrambots.doctortelegrambot.services.EmergencyService;
import org.telegrambots.doctortelegrambot.services.PatientService;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/emergency")
@RequiredArgsConstructor
public class EmergencyHelpRestController {

    private final PatientService patientService;

    private final EmergencyService emergencyService;

    @PostMapping
    public ResponseEntity<?> requestEmergencyHelp(@RequestParam String chatID) {
        Optional<Patient> optionalPatient = patientService.findPatientByChatID(Integer.parseInt(chatID));
        if (optionalPatient.isPresent()) {
            emergencyService.emergencyCallerByPatientState(optionalPatient.get());
            return ResponseEntity.ok().build();
        } else
            return ResponseEntity.notFound().build();
    }
}
