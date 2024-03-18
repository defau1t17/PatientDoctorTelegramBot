package org.telegrambots.doctortelegrambot.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.telegrambots.doctortelegrambot.entities.*;
import org.telegrambots.doctortelegrambot.repositories.PermissionRepository;
import org.testcontainers.containers.PostgreSQLContainer;

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

    private final long CHAT_ID = 111111;


    private static final PostgreSQLContainer postgresqlContainer;


    static {
        postgresqlContainer = new PostgreSQLContainer("postgres:latest")
                .withDatabaseName("telegramusers")
                .withUsername("telegrambot")
                .withPassword("telegrambot");
        postgresqlContainer.start();
    }

    @DynamicPropertySource
    public static void overrideProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", postgresqlContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", postgresqlContainer::getPassword);
        dynamicPropertyRegistry.add("spring.datasource.driver-class-name", postgresqlContainer::getDriverClassName);
    }

    @BeforeEach
    void preload() {
        doctor = new Doctor(Permission.tokenFabric(permissionRepository), 0, "test", "test", DoctorPosition.CARDIOLOGIST, 2133, DoctorShift.DAILY_SHIFT);
        patient = new Patient(Permission.tokenFabric(permissionRepository), 0, "patient", "patient", "Huge brain", PatientState.STABLE, 1333, "smart");
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
        assertTrue(doctorService.removePatientFromDoctorResponsibleList(patient.getId(), doctor.getId()));
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
