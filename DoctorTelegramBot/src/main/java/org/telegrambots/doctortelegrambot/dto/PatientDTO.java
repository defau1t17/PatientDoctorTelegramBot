package org.telegrambots.doctortelegrambot.dto;

import jakarta.persistence.*;
import lombok.Data;
import org.telegrambots.doctortelegrambot.entities.Patient;
import org.telegrambots.doctortelegrambot.entities.PatientState;
import org.telegrambots.doctortelegrambot.entities.Permission;
import org.telegrambots.doctortelegrambot.repositories.PermissionRepository;

@Data
public class PatientDTO {
    private String name;
    private String secondName;
    private String disease;
    private PatientState patientState;
    private int chamberNumber;
    private String description;


    public Patient convertDTOToPatient() {
        return new Patient(null, 1, this.name, this.secondName, this.disease, this.patientState, this.chamberNumber, this.description);
    }

}
