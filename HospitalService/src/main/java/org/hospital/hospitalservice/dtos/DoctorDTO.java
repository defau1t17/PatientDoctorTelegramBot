package org.hospital.hospitalservice.dtos;

import lombok.Data;
import org.hospital.hospitalservice.entities.Doctor;
import org.hospital.hospitalservice.entities.DoctorPosition;
import org.hospital.hospitalservice.entities.DoctorShift;

@Data
public class DoctorDTO {
    private String name;
    private String secondName;
    private DoctorPosition doctorPosition;
    private int workroom;
    private DoctorShift doctorShift;
    private String token;


    public Doctor convertDTOTOPatient() {
        return new Doctor(this.token, 0, this.name, this.secondName, this.doctorPosition, this.workroom, this.doctorShift);
    }
}
