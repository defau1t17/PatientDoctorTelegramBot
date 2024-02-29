package org.telegrambots;


import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegrambots.doctorBot.entity.Doctor;
import org.telegrambots.doctorBot.entity.DoctorPosition;
import org.telegrambots.doctorBot.entity.DoctorShift;
import org.telegrambots.doctorBot.service.DoctorRepository;
import org.telegrambots.global_entity.PermissionToken;
import org.telegrambots.global_service.TokenRepository;
import org.telegrambots.patientBot.entity.Patient;
import org.telegrambots.patientBot.entity.PatientState;
import org.telegrambots.patientBot.service.PatientRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SaveAndCreateDoctorsTest {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private TokenRepository tokenRepository;

    private static Patient patient;
    private static Patient patient2;
    private static Doctor doctor;

    @BeforeEach
    void createNewObjects() {
        patient = new Patient(123, PermissionToken.tokenFabric(tokenRepository), 0, "test", "test", "test", PatientState.Stable, 1304, "patient is dwarf");
        patient2 = new Patient(1213, PermissionToken.tokenFabric(tokenRepository), 0, "test", "test", "test", PatientState.Stable, 1304, "patient is deaf");
        List<Patient> ids = List.of(patient, patient2);
        doctor = new Doctor(1223, PermissionToken.tokenFabric(tokenRepository), 0, "testDoc", "testDoc", DoctorPosition.GYNECOLOGIST, 1002, DoctorShift.DAILY_SHIFT, ids);
    }

    @Test
    void saveEntities() {
        assertNotNull(patientRepository.save(patient));
        assertNotNull(patientRepository.save(patient2));
        assertNotNull(doctorRepository.save(doctor));
    }


    @AfterEach
    void clearData() {
        doctorRepository.delete(doctor);
        patientRepository.delete(patient);
        patientRepository.delete(patient2);
        tokenRepository.deleteAll();
    }

}
