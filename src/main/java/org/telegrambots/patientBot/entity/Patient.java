package org.telegrambots.patientBot.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.telegrambots.global_entity.PermissionToken;
import org.telegrambots.global_entity.User;


@Entity
@Data
@Table(name = "patients")
public class Patient extends User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id")
    private int id;
    private String disease;
    @Enumerated(EnumType.STRING)
    private PatientState patientState;
    private int chamberNumber;
    private String description;


    public Patient(int chatID, PermissionToken personalToken, int id, String name, String secondName, String disease, PatientState patientState, int chamberNumber, String description) {
        super(chatID, personalToken, name, secondName);
        this.id = id;
        this.disease = disease;
        this.patientState = patientState;
        this.chamberNumber = chamberNumber;
        this.description = description;
    }

    public Patient() {
        super();
    }

}
