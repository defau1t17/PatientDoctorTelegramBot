package org.hospital.hospitalservice.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "doctors")
public class Doctor {
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
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Patient> patients;
    @OneToOne
    private User user;

    public Doctor(String token, int id, String name, String secondName, DoctorPosition doctorPosition, int workroom, DoctorShift doctorShift) {
        this.user = new User(name, secondName, token);
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
//
//    @Override
//    public String toString() {
//        return super.toString() +
//                "\nid : [%s]\nposition : [%s]\nworkroom : [%s]\nshift : [%s]\nshift status : [%s]"
//                        .formatted(
//                                this.id,
//                                this.doctorPosition.getPosition(),
//                                this.workroom,
//                                this.shiftStatus.toString()
//                        );
//    }
}