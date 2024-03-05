package org.telegrambots.doctortelegrambot.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegrambots.doctortelegrambot.entities.*;
import org.telegrambots.doctortelegrambot.repositories.PermissionRepository;

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

    @Autowired
    private AuthenticationService authenticationService;

    private final int CHAT_ID = 111111;

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
    void findDoctorByChatID() {
        authenticationService.authenticate(CHAT_ID, doctor.getPersonalToken().getPermissionToken());
        assertNotNull(doctorService.findDoctorByChatID(CHAT_ID).get());
    }

    @Test
    void openDoctorShiftTest() {
        authenticationService.authenticate(CHAT_ID, doctor.getPersonalToken().getPermissionToken());
        assertEquals(doctorService.doctorShiftManipulation(CHAT_ID).getShiftStatus(), ShiftStatus.OPENED);
    }

    @Test
    void closeDoctorShiftTest() {
        authenticationService.authenticate(CHAT_ID, doctor.getPersonalToken().getPermissionToken());
        doctorService.doctorShiftManipulation(CHAT_ID);
        assertEquals(doctorService.doctorShiftManipulation(CHAT_ID).getShiftStatus(), ShiftStatus.CLOSED);

    }


    @AfterEach
    void clear() {
        patientService.delete(patient);
        doctorService.delete(doctor);
        permissionRepository.delete(patient.getPersonalToken());
        permissionRepository.delete(doctor.getPersonalToken());
    }

}
