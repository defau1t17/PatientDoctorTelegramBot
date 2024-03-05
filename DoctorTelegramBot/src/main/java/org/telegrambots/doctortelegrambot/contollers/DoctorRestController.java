package org.telegrambots.doctortelegrambot.contollers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegrambots.doctortelegrambot.entities.Doctor;
import org.telegrambots.doctortelegrambot.services.DoctorService;

@RestController
@RequestMapping("/api/v1/doctor")
@RequiredArgsConstructor
public class DoctorRestController {

    private final DoctorService doctorService;

    @GetMapping
    public ResponseEntity<?> getSomeMessage() {
        return ResponseEntity.ok("good answer");
    }

    @PostMapping
    public ResponseEntity<?> updateDoctorShift(@RequestParam String chatID) {
        System.out.printf("incoming chatid %s%n", chatID);
        Doctor updatedDoctor = doctorService.doctorShiftManipulation(Integer.parseInt(chatID));
        return updatedDoctor != null ?
                ResponseEntity.ok(updatedDoctor) :
                ResponseEntity.notFound().build();
    }

}
