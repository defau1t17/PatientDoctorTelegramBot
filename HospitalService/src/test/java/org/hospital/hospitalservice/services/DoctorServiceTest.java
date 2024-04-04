package org.hospital.hospitalservice.services;

import org.hospital.hospitalservice.entities.*;
import org.hospital.hospitalservice.repositories.DoctorRepository;
import org.hospital.hospitalservice.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepository mockRepository;
    @Mock
    private PatientService mockPatientService;
    @Mock
    private UserRepository mockUserRepository;

    private DoctorService doctorServiceUnderTest;

    private Doctor doctor;


    @BeforeEach
    void setUp() {
        doctorServiceUnderTest = new DoctorService(mockRepository, mockPatientService, mockUserRepository);
        doctor = new Doctor("test-token", 1, "test-token", "test-token", DoctorPosition.CHIEF_PHYSICIAN, 23, DoctorShift.DAILY_SHIFT);
        doctor.setUser(new User("test", "test", "test-token"));
    }

    @Test
    void testFindAll() {
        final Page<Doctor> doctors = new PageImpl<>(List.of(new Doctor(), new Doctor()));
        when(mockRepository.findAll(PageRequest.of(0, 1))).thenReturn(doctors);
        final Page<Doctor> result = doctorServiceUnderTest.findAll(0, 1);
        assertNotEquals(result.getContent(), List.of());
    }

    @Test
    void testFindAll_DoctorRepositoryReturnsNoItems() {
        when(mockRepository.findAll(PageRequest.of(0, 1))).thenReturn(new PageImpl<>(Collections.emptyList()));
        final Page<Doctor> result = doctorServiceUnderTest.findAll(0, 1);
        assertEquals(result.getContent(), List.of());
    }


    @Test
    void testCreate() {
        Doctor optionalDoctor = spy();
        optionalDoctor.setUser(spy());

        when(mockUserRepository.save(any())).thenReturn(new User());
        when(mockRepository.save(optionalDoctor)).thenReturn(doctor);
        final Doctor result = doctorServiceUnderTest.create(optionalDoctor);

        assertEquals(doctor, result);

        verify(mockUserRepository).save(new User());
        verify(mockRepository).save(optionalDoctor);
    }

    @Test
    void testUpdate() {
        Doctor optionalDoctor = new Doctor();
        optionalDoctor.setUser(new User());

        when(mockUserRepository.save(new User())).thenReturn(new User());
        when(mockRepository.save(optionalDoctor)).thenReturn(doctor);
        final Doctor result = doctorServiceUnderTest.create(optionalDoctor);

        assertEquals(doctor, result);

        verify(mockUserRepository).save(new User());
        verify(mockRepository).save(optionalDoctor);
    }

    @Test
    void testDelete() {
        doctorServiceUnderTest.delete(new Doctor());
        verify(mockRepository).delete(new Doctor());
    }

    @Test
    void testDeleteByChatID() {
        doctorServiceUnderTest.deleteByChatID(anyLong());
        verify(mockRepository).deleteDoctorByChatID(anyLong());
    }

    @Test
    void testDeleteByID() {
        doctorServiceUnderTest.deleteByID(anyLong());
        verify(mockRepository).deleteById(anyLong());
    }

    @Test
    void testFindByChatID() {
        when(mockRepository.findDoctorByChatID(anyLong())).thenReturn(Optional.of(doctor));
        final Optional<Doctor> result = doctorServiceUnderTest.findByChatID(anyLong());
        assertTrue(result.isPresent());
        assertEquals(doctor, result.get());
    }

    @Test
    void testFindByChatID_DoctorRepositoryReturnsAbsent() {
        when(mockRepository.findDoctorByChatID(0L)).thenReturn(Optional.empty());
        final Optional<Doctor> result = doctorServiceUnderTest.findByChatID(0L);
        assertEquals(Optional.empty(), result);
    }

    @Test
    void testFindByID() {
        when(mockRepository.findById(anyLong())).thenReturn(Optional.of(doctor));
        final Optional<Doctor> result = doctorServiceUnderTest.findByID(anyLong());
        assertTrue(result.isPresent());
        assertEquals(doctor, result.get());
    }

    @Test
    void testFindByID_DoctorRepositoryReturnsAbsent() {
        when(mockRepository.findById(anyLong())).thenReturn(Optional.empty());
        final Optional<Doctor> result = doctorServiceUnderTest.findByID(anyLong());
        assertEquals(Optional.empty(), result);
    }

    @Test
    void testValidateBeforeSave() {
        final boolean result = doctorServiceUnderTest.validateBeforeSave(doctor);
        assertTrue(result);
    }

    @Test
    void testChangeDoctorShiftStatus() {
        Doctor optionalDoctor = spy(doctor);
        optionalDoctor.setShiftStatus(ShiftStatus.CLOSED);
        when(mockRepository.save(any())).thenReturn(optionalDoctor);
        final Doctor result = doctorServiceUnderTest.changeDoctorShiftStatus(doctor);
        assertNotEquals(doctor, result);
    }

    @Test
    void testAssignPatientToDoctor() {
        final Patient patient = spy(Patient.class);
        patient.setUser(spy(User.class));

        Doctor optionalDoctor = spy(doctor);
        optionalDoctor.setPatients(List.of(patient));

        when(mockPatientService.findByID(anyLong())).thenReturn(Optional.of(patient));
        when(doctorServiceUnderTest.findByID(anyLong())).thenReturn(Optional.of(doctor));
        when(mockRepository.save(any())).thenReturn(doctor);

        final boolean result = doctorServiceUnderTest.assignPatientToDoctor(0L, 0L);
        assertTrue(result);
        assertNotEquals(doctor.getPatients(), List.of());
        assertEquals(doctor.getPatients().size(), 1);
        assertEquals(doctor.getPatients().get(0), patient);
    }

    @Test
    void testAssignPatientToDoctor_DoctorRepositoryFindByIdReturnsAbsent() {
        when(mockPatientService.findByID(anyLong())).thenReturn(Optional.empty());
        when(doctorServiceUnderTest.findByID(anyLong())).thenReturn(Optional.empty());
        final boolean result = doctorServiceUnderTest.assignPatientToDoctor(0L, 0L);
        assertFalse(result);
    }

    @Test
    void testUnhookPatientFromDoctor() {
        final Patient patient = spy(Patient.class);
        patient.setUser(spy(User.class));

        when(mockPatientService.findByID(anyLong())).thenReturn(Optional.of(patient));
        when(doctorServiceUnderTest.findByID(anyLong())).thenReturn(Optional.of(doctor));
        when(mockRepository.save(any())).thenReturn(doctor);

        final boolean result = doctorServiceUnderTest.unhookPatientFromDoctor(0L, 0L);
        assertTrue(result);
        assertEquals(doctor.getPatients(), List.of());
        assertEquals(doctor.getPatients().size(), 0);
    }

    @Test
    void testUnhookPatientFromDoctor_DoctorRepositoryFindByIdReturnsAbsent() {
        when(mockPatientService.findByID(anyLong())).thenReturn(Optional.empty());
        when(doctorServiceUnderTest.findByID(anyLong())).thenReturn(Optional.empty());
        final boolean result = doctorServiceUnderTest.unhookPatientFromDoctor(0L, 0L);
        assertFalse(result);
    }
}
