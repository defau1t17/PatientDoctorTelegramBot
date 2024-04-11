package org.emergency.emergency.endpoints;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.emergency.emergency.dto.DoctorDTO;
import org.emergency.emergency.dto.PatientDTO;
import org.emergency.emergency.service.PatientStateResolverService;
import org.emergency.emergency.service.RequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "Emergency Endpoint", description = "Request emergency help")
@RestController
@RequestMapping("/help")
@RequiredArgsConstructor
public class EmergencyEndpoint {

    private final RequestService requestService;

    private final PatientStateResolverService resolverService;

    @Operation(summary = "Request for emergency help", parameters = @Parameter(name = "chatID", example = "131231"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404")
    })
    @PostMapping
    public ResponseEntity<?> getHelp(@RequestParam long chatID) {
        Optional<PatientDTO> patientByChatID = requestService.getPatientByChatID(chatID);
        if (patientByChatID.isEmpty()) {
            return ResponseEntity
                    .notFound()
                    .build();
        }
        Optional<List<DoctorDTO>> doctorsByPatientID = requestService.getDoctorsByPatientID(patientByChatID.get().getId());
        if (doctorsByPatientID.isPresent()) {
            resolverService.emergencyCallerByPatientState(doctorsByPatientID.get(), patientByChatID.get());
            return ResponseEntity
                    .ok()
                    .build();
        } else return ResponseEntity
                .notFound()
                .build();

    }

}
