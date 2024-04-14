package org.hospital.hospitalservice.endpoints;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.hospital.hospitalservice.dtos.PatientDTO;
import org.hospital.hospitalservice.entities.Patient;
import org.hospital.hospitalservice.entities.User;
import org.hospital.hospitalservice.services.PatientService;
import org.hospital.hospitalservice.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@Tag(name = "Patient Endpoint", description = "Manage hospital patients")
@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientEndpoint {

    private final PatientService patientService;

    private final UserService userService;

    @Operation(summary = "Get patient by ID", parameters = @Parameter(name = "id", required = true, example = "13213"), tags = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Patient.class))),
            @ApiResponse(responseCode = "404")
    })
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

    @Operation(summary = "Get patient by chat ID", parameters = @Parameter(name = "chatID", required = true, example = "13213"), tags = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Patient.class))),
            @ApiResponse(responseCode = "404")
    })
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

    @Operation(summary = "Get page of patients", tags = "GET", responses = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Patient[].class))
            )
    })
    @GetMapping
    public ResponseEntity<?> getPatients(@RequestParam(value = "page") Optional<Integer> page, @RequestParam(value = "size") Optional<Integer> size) {
        return ResponseEntity
                .ok(patientService.findAll(page.orElse(0), size.orElse(5)));
    }


    @Operation(summary = "Create patient", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(implementation = PatientDTO.class)), required = true), tags = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Patient.class))),
            @ApiResponse(responseCode = "400")
    })
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

    @Operation(summary = "Update patient chat ID", parameters = {@Parameter(name = "chatID", example = "1"), @Parameter(name = "token", example = "8fb00b2d-1de8-4a72-a227-9557402de126")}, tags = "PATCH")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400")
    })
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

    @Operation(summary = "Delete patient by ID", parameters = @Parameter(name = "id", example = "1"), tags = "DELETE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404")
    })
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

    @Operation(summary = "Delete patient by chat ID", parameters = @Parameter(name = "chatID", example = "1"), tags = "DELETE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404")
    })
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
