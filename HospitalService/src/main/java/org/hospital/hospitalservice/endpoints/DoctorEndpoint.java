package org.hospital.hospitalservice.endpoints;

import lombok.RequiredArgsConstructor;
import org.hospital.hospitalservice.dtos.DoctorDTO;
import org.hospital.hospitalservice.entities.Doctor;
import org.hospital.hospitalservice.entities.User;
import org.hospital.hospitalservice.services.DoctorService;
import org.hospital.hospitalservice.services.UserService;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class DoctorEndpoint {

    private final DoctorService doctorService;

    private final UserService userService;

    @Cacheable(value = "pageWithDoctors")
    @GetMapping
    public ResponseEntity<?> getAllDoctors(@RequestParam(value = "pageNumber") Optional<Integer> pageNumber,
                                           @RequestParam(value = "pageSize") Optional<Integer> pageSize) {
        return ResponseEntity
                .ok(doctorService.findAll(pageNumber.orElse(0), pageSize.orElse(5)));
    }

    @Cacheable(value = "doctorByID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getDoctorByID(@PathVariable(value = "id") long id) {
        Optional<Doctor> optionalDoctor = doctorService.findByID(id);
        return optionalDoctor.isPresent() ?
                ResponseEntity
                        .ok(optionalDoctor.get()) :
                ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .build();
    }

    @Cacheable(value = "doctorByChatID")
    @GetMapping("/chat/{chatID}")
    public ResponseEntity<?> findDoctorByChatID(@PathVariable(value = "chatID") long chatID) {
        Optional<Doctor> optionalDoctor = doctorService.findByChatID(chatID);
        return optionalDoctor.isPresent() ?
                ResponseEntity
                        .ok(optionalDoctor.get()) :
                ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .build();
    }

    @PostMapping
    public ResponseEntity<?> createNewDoctor(@RequestBody DoctorDTO doctor) {
        System.out.println(doctor.toString());
        Doctor potentialDoctor = doctor.convertDTOTOPatient();
        if (doctorService.validateBeforeSave(potentialDoctor)) {
            return ResponseEntity
                    .ok(doctorService.create(potentialDoctor));
        } else
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
    }

    @PostMapping("/{id}/add/patient/{patientID}")
    public ResponseEntity<?> assignPatientToDoctor(@PathVariable(value = "id") long doctorID, @PathVariable(value = "patientID") long patientID) {
        return doctorService.assignPatientToDoctor(patientID, doctorID) ?
                ResponseEntity
                        .ok("assigned successfully") :
                ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .build();
    }

    @CachePut(value = "doctor", key = "shiftStatus")
    @PostMapping("/{chatID}/shift")
    public ResponseEntity<?> updateDoctorShift(@PathVariable(value = "chatID") long chatID) {
        Optional<Doctor> optionalDoctor = doctorService.findByChatID(chatID);
        return optionalDoctor.isPresent() ?
                ResponseEntity
                        .ok(doctorService.changeDoctorShiftStatus(optionalDoctor.get())) :
                ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDoctor(@PathVariable(value = "id") long id) {
        Optional<Doctor> optionalDoctor = doctorService.findByID(id);
        if (optionalDoctor.isPresent()) {
            doctorService.deleteByID(id);
            return ResponseEntity
                    .ok("deleted successfully");
        } else
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
    }

    @DeleteMapping("/chat/{chatID}")
    public ResponseEntity<?> deleteDoctorByChatID(@PathVariable(value = "chatID") long chatID) {
        Optional<Doctor> optionalDoctor = doctorService.findByChatID(chatID);
        if (optionalDoctor.isPresent()) {
            doctorService.deleteByChatID(chatID);
            return ResponseEntity
                    .ok("deleted successfully");
        } else
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
    }

    @DeleteMapping("/{id}/remove/patient/{patientID}")
    public ResponseEntity<?> unhookPatientToDoctor(@PathVariable(value = "id") long doctorID, @PathVariable(value = "patientID") long patientID) {
        return doctorService.unhookPatientFromDoctor(patientID, doctorID) ?
                ResponseEntity
                        .ok("unhooked successfully") :
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


}
