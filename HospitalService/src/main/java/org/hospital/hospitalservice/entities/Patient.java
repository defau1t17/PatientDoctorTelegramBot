package org.hospital.hospitalservice.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;


@Entity
@Data
@Table(name = "patients")
public class Patient implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id")
    private int id;
    private String disease;
    @Enumerated(EnumType.STRING)
    private PatientState patientState;
    private int chamberNumber;
    private String description;
    @OneToOne
    private User user;


    public Patient(String token, int id, String name, String secondName, String disease, PatientState patientState, int chamberNumber, String description) {
        this.user = new User(name, secondName, token);
        this.id = id;
        this.disease = disease;
        this.patientState = patientState;
        this.chamberNumber = chamberNumber;
        this.description = description;
    }

    public Patient() {
        super();
    }

    @Override
    public String toString() {
        return super.toString() + "\nID : [%s]\nDisease: [%s]\nState: [%s]\nChamber: [%s]\nDescription: [%s]\n"
                .formatted(
                        this.id,
                        this.disease,
                        this.patientState,
                        this.chamberNumber,
                        this.description);
    }
}
