package org.hospital.hospitalservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hospital.hospitalservice.entities.Patient;
import org.hospital.hospitalservice.entities.PatientState;

@Data
@AllArgsConstructor
public class PatientDTO {
    private String name;
    private String secondName;
    private String disease;
    private PatientState patientState;
    private int chamberNumber;
    private String description;
    private String token;


    public Patient convertDTOToPatient() {
        return new Patient(
                this.token,
                0,
                this.name,
                this.secondName,
                this.disease,
                this.patientState,
                this.chamberNumber,
                this.description
        );
    }

    public static PatientDTO covertPatientToDTO(Patient patient) {
        return new PatientDTO(
                patient.getUser().getName(),
                patient.getUser().getSecondName(),
                patient.getDisease(),
                patient.getPatientState(),
                patient.getChamberNumber(),
                patient.getDescription(),
                patient.getUser().getToken()
        );
    }
}
