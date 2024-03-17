package org.telegrambots.doctortelegrambot.services;

import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegrambots.doctortelegrambot.entities.Patient;
import org.telegrambots.doctortelegrambot.entities.PatientState;
import org.telegrambots.doctortelegrambot.entities.Permission;
import org.telegrambots.doctortelegrambot.repositories.PermissionRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PatientServiceTest {

    @Autowired
    private PatientService patientService;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private AuthenticationService authenticationService;

    private final long CHAT_ID = 11111223;
    private Patient patient;

    @BeforeEach
    void init() {
        patient = new Patient(Permission.tokenFabric(permissionRepository), 0, "patient", "patient", "Huge brain", PatientState.STABLE, 133, "smart");
        patient = patientService.create(patient);
    }

    @Test
    void findDoctorByIDTest() {
        assertNotNull(patientService.findByID(patient.getId()).get());
    }

    @Test
    void findAllDoctorsTest() {
        assertNotNull(patientService.findAll());
    }

    @Test
    void findAllPatientsByPage() {
        assertNotNull(patientService.findAllByPage(0, 1).getContent());
    }

    @Test
    void findDoctorByChatID() {
        authenticationService.authenticate(CHAT_ID, patient.getPersonalToken().getPermissionToken());
        assertNotNull(patientService.findPatientByChatID(CHAT_ID).get());
    }

    @Test
    void validationSuccessTest() {
        assertTrue(patientService.validatePatientBeforeSave(patient));
    }

    @Test
    void validationFailureTest() {
        assertFalse(patientService.validatePatientBeforeSave(new Patient()));
    }

    @AfterEach
    void clear() {
        patientService.delete(patient);
        permissionRepository.delete(patient.getPersonalToken());
    }

}
