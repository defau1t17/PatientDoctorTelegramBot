package org.telegrambots.doctortelegrambot.contollers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegrambots.doctortelegrambot.entities.Doctor;
import org.telegrambots.doctortelegrambot.services.DoctorService;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/doctor")
@RequiredArgsConstructor
public class DoctorRestController {

    private final DoctorService doctorService;

    @GetMapping
    public ResponseEntity<?> getAllDoctors() {
        return ResponseEntity.ok(doctorService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDoctorByID(@PathVariable String id) {
        Optional<Doctor> optionalDoctor = doctorService.findByID(Integer.parseInt(id));
        return optionalDoctor.isPresent() ?
                ResponseEntity
                        .ok(optionalDoctor.get()) :
                ResponseEntity
                        .notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> createNewDoctor(@RequestBody Doctor doctor) {
        if (doctorService.validateDoctorBeforeSave(doctor)) {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(doctorService.create(doctor));
        } else
            return ResponseEntity
                    .badRequest()
                    .build();
    }

    @PostMapping("/shift")
    public ResponseEntity<?> updateDoctorShift(@RequestParam(name = "chatID") String chatID) {
        Doctor updatedDoctor = doctorService.doctorShiftManipulation(Integer.parseInt(chatID));
        return updatedDoctor != null ?
                ResponseEntity
                        .ok(updatedDoctor) :
                ResponseEntity
                        .notFound().build();
    }



}
