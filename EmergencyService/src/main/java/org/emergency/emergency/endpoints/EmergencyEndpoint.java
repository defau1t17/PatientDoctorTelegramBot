package org.emergency.emergency.endpoints;

import lombok.RequiredArgsConstructor;
import org.emergency.emergency.dto.DoctorDTO;
import org.emergency.emergency.dto.PatientDTO;
import org.emergency.emergency.service.PatientStateResolverService;
import org.emergency.emergency.service.RequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/help")
@RequiredArgsConstructor
public class EmergencyEndpoint {

    private final RequestService requestService;

    private final PatientStateResolverService resolverService;

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
