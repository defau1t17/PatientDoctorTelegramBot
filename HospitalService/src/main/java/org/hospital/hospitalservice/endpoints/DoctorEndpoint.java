package org.hospital.hospitalservice.endpoints;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.hospital.hospitalservice.dtos.DoctorDTO;
import org.hospital.hospitalservice.entities.Doctor;
import org.hospital.hospitalservice.entities.User;
import org.hospital.hospitalservice.services.DoctorService;
import org.hospital.hospitalservice.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "Doctor Endpoint", description = "Manage hospital doctors")
@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class DoctorEndpoint {

    private final DoctorService doctorService;

    private final UserService userService;


    @Operation(summary = "Get page of doctors", tags = "GET", responses = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Doctor[].class))
            )
    })
    @GetMapping
    public ResponseEntity<?> getAllDoctors(@RequestParam(value = "pageNumber") Optional<Integer> pageNumber,
                                           @RequestParam(value = "pageSize") Optional<Integer> pageSize) {
        return ResponseEntity
                .ok(doctorService.findAll(pageNumber.orElse(0), pageSize.orElse(5)));
    }

    @Operation(summary = "Get doctor by ID", parameters = @Parameter(name = "id", required = true, example = "13213"), tags = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Doctor.class))),
            @ApiResponse(responseCode = "404")
    })
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

    @Operation(summary = "Get doctor by patient ID", parameters = @Parameter(name = "id", required = true, example = "13213"), tags = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Doctor[].class))),
            @ApiResponse(responseCode = "404")
    })
    @GetMapping("/patient/{id}")
    public ResponseEntity<?> getAllDoctorsByPatient(@PathVariable(value = "id") long id) {
        Optional<List<Doctor>> allByPatientID = doctorService.findAllDoctorsByPatientID(id);
        return allByPatientID.isPresent() ?
                ResponseEntity
                        .ok(allByPatientID.get()) :
                ResponseEntity
                        .notFound()
                        .build();
    }

    @Operation(summary = "Get doctor by chat ID", parameters = @Parameter(name = "chatID", required = true, example = "13213"), tags = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Doctor.class))),
            @ApiResponse(responseCode = "404")
    })
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

    @Operation(summary = "Create doctor", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(implementation = DoctorDTO.class)), required = true), tags = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Doctor.class))),
            @ApiResponse(responseCode = "400")
    })
    @PostMapping
    public ResponseEntity<?> createNewDoctor(@RequestBody DoctorDTO doctor) {
        Doctor potentialDoctor = doctor.convertDTOTOPatient();
        if (doctorService.validateBeforeSave(potentialDoctor)) {
            return ResponseEntity
                    .ok(doctorService.create(potentialDoctor));
        } else
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
    }

    @Operation(summary = "Assign patient to doctor", parameters = {@Parameter(name = "id", example = "1"), @Parameter(name = "patientID", example = "2")}, tags = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400")
    })
    @PostMapping("/{id}/add/patient/{patientID}")
    public ResponseEntity<?> assignPatientToDoctor(@PathVariable(value = "id") long doctorID, @PathVariable(value = "patientID") long patientID) {
        return doctorService.assignPatientToDoctor(patientID, doctorID) ?
                ResponseEntity
                        .ok("assigned successfully") :
                ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .build();
    }

    @Operation(summary = "Update doctor shift", parameters = @Parameter(name = "chatID", example = "1"), tags = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Doctor.class))),
            @ApiResponse(responseCode = "400")
    })
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

    @Operation(summary = "Delete doctor by ID", parameters = @Parameter(name = "id", example = "1"), tags = "DELETE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404")
    })
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

    @Operation(summary = "Delete doctor by chat ID", parameters = @Parameter(name = "id", example = "1"), tags = "DELETE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404")
    })
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

    @Operation(summary = "Unhook patient from doctor", parameters = {@Parameter(name = "id", example = "1"), @Parameter(name = "patientID", example = "1")}, tags = "DELETE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400")
    })
    @DeleteMapping("/{id}/remove/patient/{patientID}")
    public ResponseEntity<?> unhookPatientToDoctor(@PathVariable(value = "id") long doctorID, @PathVariable(value = "patientID") long patientID) {
        return doctorService.unhookPatientFromDoctor(patientID, doctorID) ?
                ResponseEntity
                        .ok("unhooked successfully") :
                ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .build();
    }

    @Operation(summary = "Update doctors chat ID", parameters = {@Parameter(name = "chatID", example = "1"), @Parameter(name = "token", example = "165dde87-1039-471c-bd58-d9134784100e")},tags = "PATCH")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404")
    })
    @PatchMapping
    public ResponseEntity<?> updateDoctorChatID(@RequestParam long chatID, @RequestParam String token) {
        Optional<User> optionalUser = userService.findUserByToken(token);
        return optionalUser.isPresent() ?
                ResponseEntity
                        .ok(userService.updateUserChatID(optionalUser.get(), chatID)) :
                ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .build();
    }


}
