package org.hospital.hospitalservice.services;

import org.hospital.hospitalservice.entities.Doctor;
import org.hospital.hospitalservice.entities.DoctorPosition;
import org.hospital.hospitalservice.entities.DoctorShift;
import org.hospital.hospitalservice.repositories.DoctorRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringBootTest
class DoctorServiceTest {
    @Mock
    DoctorRepository repository;
    @Mock
    PatientService patientService;
    @InjectMocks
    DoctorService doctorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        when(repository.findAll()).thenReturn(null);
        Page<Doctor> result = doctorService.findAll(0, 1);
        Assertions.assertEquals(null, result);
    }

    @Test
    void testCreate() {
        when(repository.save(any())).thenReturn(new Doctor());

        Doctor result = doctorService.create(new Doctor("token", 0, "name", "secondName", null, 0, null));
        Assertions.assertEquals(new Doctor("token", 0, "name", "secondName", null, 0, null), result);
    }

    @Test
    void testUpdate() {
        when(repository.save(any())).thenReturn(new Doctor());

        Doctor result = doctorService.update(new Doctor("token", 0, "name", "secondName", null, 0, null));
        Assertions.assertEquals(new Doctor("token", 0, "name", "secondName", null, 0, null), result);
    }

    @Test
    void testDelete() {
        doctorService.delete(new Doctor("token", 0, "name", "secondName", null, 0, null));
        verify(repository).delete(any());
    }

    @Test
    void testDeleteByChatID() {
        doctorService.deleteByChatID(Long.valueOf(1));
        verify(repository).deleteDoctorByChatID(anyLong());
    }

    @Test
    void testDeleteByID() {
        doctorService.deleteByID(1L);
        verify(repository).deleteById(any());
    }

    @Test
    void testFindByChatID() {
        when(repository.findDoctorByChatID(anyLong())).thenReturn(Optional.empty());

        Optional<Doctor> result = doctorService.findByChatID(1L);
        Assertions.assertEquals(Optional.empty(), result);
    }

    @Test
    void testFindByID() {
        when(repository.findById(any())).thenReturn(Optional.empty());
        Optional<Doctor> result = doctorService.findByID(1L);
        Assertions.assertEquals(Optional.empty(), result);
    }

    @Test
    void testValidateBeforeSave() {
        boolean result = doctorService.validateBeforeSave(new Doctor("token", 0, "name", "secondName", DoctorPosition.CHIEF_PHYSICIAN, 0, DoctorShift.DAILY_SHIFT));
        Assertions.assertEquals(true, result);
    }

    @Test
    void testChangeDoctorShiftStatus() {
        when(repository.save(any())).thenReturn(new Doctor());

        Doctor result = doctorService.changeDoctorShiftStatus(new Doctor("token", 0, "name", "secondName", null, 0, null));
        Assertions.assertEquals(new Doctor("token", 0, "name", "secondName", null, 0, null), result);
    }

    @Test
    void testAssignPatientToDoctor() {
        when(repository.save(any())).thenReturn(new Doctor());
        when(repository.findById(any())).thenReturn(null);
        when(patientService.findByID(anyLong())).thenReturn(null);

        boolean result = doctorService.assignPatientToDoctor(0L, 0L);
        Assertions.assertEquals(true, result);
    }

    @Test
    void testUnhookPatientFromDoctor() {
        when(repository.save(any())).thenReturn(new Doctor());
        when(repository.findById(any())).thenReturn(null);
        when(patientService.findByID(anyLong())).thenReturn(null);

        boolean result = doctorService.unhookPatientFromDoctor(0L, 0L);
        Assertions.assertEquals(true, result);
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme