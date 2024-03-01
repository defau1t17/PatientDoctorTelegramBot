package org.telegrambots.doctortelegrambot.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegrambots.doctortelegrambot.entity.*;
import org.telegrambots.doctortelegrambot.repository.PermissionRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DoctorServiceTest {

    private Doctor doctor;

    private Patient patient;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private PermissionRepository permissionRepository;

    @BeforeEach
    void preload() {
        doctor = new Doctor(Permission.tokenFabric(permissionRepository), 0, "test", "test", DoctorPosition.CARDIOLOGIST, 2133, DoctorShift.DAILY_SHIFT);
        patient = new Patient(Permission.tokenFabric(permissionRepository), 0, "patient", "patient", "Huge brain", PatientState.Stable, 1333, "smart");
        doctorService.create(doctor);
        patientService.create(patient);
    }


    @Test
    void findDoctorByIDTest() {
        assertNotNull(doctorService.findByID(doctor.getId()).get());
    }

    @Test
    void findAllDoctorsTest() {
        assertNotNull(doctorService.findAll());
    }

    @Test
    void addAndRemovePatientFromDoctorResponsibleListTest() {
        assertTrue(doctorService.addNewPatientIntoDoctorResponsibleList(patient.getId(), doctor.getId()));
        assertTrue(doctorService.removePatientIntoDoctorResponsibleList(patient.getId(), doctor.getId()));
    }

    @Test
    void openDoctorShiftTest() {
        assertTrue(doctorService.doctorShiftManipulation(doctor.getId(), "OPEN"));
        System.out.println(doctorService.findByID(doctor.getId()));
    }

    @Test
    void closeDoctorShiftTest() {
        assertTrue(doctorService.doctorShiftManipulation(doctor.getId(), "CLOSE"));
        System.out.println(doctor);
    }


    @AfterEach
    void clear() {
       patientService.delete(patient);
       doctorService.delete(doctor);
    }

}
