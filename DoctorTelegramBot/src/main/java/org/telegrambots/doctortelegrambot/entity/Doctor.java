package org.telegrambots.doctortelegrambot.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
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
    @Enumerated(EnumType.STRING)
    private ShiftStatus shiftStatus;
    @OneToMany(fetch = FetchType.EAGER)
    private List<Patient> patients;

    public Doctor(Permission personalToken, int id, String name, String secondName, DoctorPosition doctorPosition, int workroom, DoctorShift doctorShift) {
        super(personalToken, name, secondName);
        this.id = id;
        this.doctorPosition = doctorPosition;
        this.workroom = workroom;
        this.doctorShift = doctorShift;
        this.patients = new ArrayList<>();
        this.shiftStatus = ShiftStatus.CLOSED;
    }

    public Doctor() {
        super();
    }

    public void addPatient(Patient patient) {
        this.patients.add(patient);
    }

    public void removePatient(Patient patient) {
        this.patients.remove(patient);
    }
}