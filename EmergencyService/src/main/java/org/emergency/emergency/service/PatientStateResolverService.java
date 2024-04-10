package org.emergency.emergency.service;

import lombok.RequiredArgsConstructor;
import org.emergency.emergency.dto.DoctorDTO;
import org.emergency.emergency.dto.EmergencyDTO;
import org.emergency.emergency.dto.PatientDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientStateResolverService {

    private final RabbitTemplate rabbitTemplate;


    public void emergencyCallerByPatientState(List<DoctorDTO> doctors, PatientDTO patient) {
        Optional<DoctorDTO> nurse = doctors.stream().filter(param -> param.getDoctorPosition().equals("NURSE")).findAny();
        Optional<DoctorDTO> doctor = doctors.stream().filter(param -> param.getDoctorPosition().equals("SURGEON")).findAny();
        Optional<DoctorDTO> paramedic = doctors.stream().filter(param -> param.getDoctorPosition().equals("PARAMEDIC")).findAny();
        switch (patient.getPatientState()) {
            case "STABLE" -> {
                callForObject(new long[]{nurse.get().getUser().getChatID()}, patient, "nurse");
            }
            case "HARD" -> {
                callForObject(new long[]{nurse.get().getUser().getChatID(), doctor.get().getUser().getChatID()}, patient, "nurse", "doctor");
            }
            case "CRITICAL" -> {
                callForObject(new long[]{nurse.get().getUser().getChatID(), doctor.get().getUser().getChatID(), paramedic.get().getUser().getChatID()}, patient, "nurse", "doctor", "paramedic");
            }
        }
    }

    private void callForObject(long[] doctorChatID, PatientDTO patientDTO, String... doctors) {
        for (int i = 0; i < doctorChatID.length; i++) {
            rabbitTemplate.convertAndSend(doctors[i], new EmergencyDTO(patientDTO.getChamberNumber(), doctorChatID[i]));
        }
    }

}
