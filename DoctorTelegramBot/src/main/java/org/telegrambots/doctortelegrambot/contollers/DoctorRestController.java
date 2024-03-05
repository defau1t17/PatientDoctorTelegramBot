package org.telegrambots.doctortelegrambot.contollers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegrambots.doctortelegrambot.entities.DoctorShift;
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
    public ResponseEntity<?> updateShift(@RequestParam String doctorID, @RequestParam String shiftCommand) {
        return doctorService.doctorShiftManipulation(Integer.parseInt(doctorID), shiftCommand) ?
                ResponseEntity.ok().build() :
                ResponseEntity.notFound().build();
    }

}
