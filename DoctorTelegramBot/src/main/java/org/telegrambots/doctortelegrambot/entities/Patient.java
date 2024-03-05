package org.telegrambots.doctortelegrambot.entities;

import jakarta.persistence.*;
import lombok.Data;


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


    public Patient(Permission personalToken, int id, String name, String secondName, String disease, PatientState patientState, int chamberNumber, String description) {
        super(personalToken, name, secondName);
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
