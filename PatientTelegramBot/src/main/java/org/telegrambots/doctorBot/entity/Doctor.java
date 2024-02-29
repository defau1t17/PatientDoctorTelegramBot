package org.telegrambots.doctorBot.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.telegrambots.global_entity.PermissionToken;
import org.telegrambots.global_entity.User;
import org.telegrambots.patientBot.entity.Patient;

import java.util.List;

@Data
@Entity
@Table(name = "doctors")
public class Doctor extends User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doctor_id")
    private int id;
    @Enumerated(EnumType.STRING)
    private DoctorPosition doctorPosition;
    private int workroom;
    @Enumerated(EnumType.STRING)
    private DoctorShift doctorShift;
    @OneToMany
    private List<Patient> patients;

    public Doctor(int chatID, PermissionToken personalToken, int id, String name, String secondName, DoctorPosition doctorPosition, int workroom, DoctorShift doctorShift, List<Patient> patients) {
        super(chatID, personalToken, name, secondName);
        this.id = id;
        this.doctorPosition = doctorPosition;
        this.workroom = workroom;
        this.doctorShift = doctorShift;
        this.patients = patients;
    }

    public Doctor() {
        super();
    }
}
