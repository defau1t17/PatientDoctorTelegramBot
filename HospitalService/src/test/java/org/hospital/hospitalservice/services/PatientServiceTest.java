package org.hospital.hospitalservice.services;

import org.hospital.hospitalservice.entities.Patient;
import org.hospital.hospitalservice.entities.PatientState;
import org.hospital.hospitalservice.entities.User;
import org.hospital.hospitalservice.repositories.PatientRepository;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository mockRepository;
    @Mock
    private UserRepository mockUserRepository;

    private PatientService patientServiceUnderTest;

    private Patient patient;

    @BeforeEach
    void setUp() {
        patientServiceUnderTest = new PatientService(mockRepository, mockUserRepository);
        patient = spy(Patient.class);
        patient.setUser(spy(User.class));
    }

    @Test
    void testFindAll() {
        final Page<Patient> patients = new PageImpl<>(List.of(new Patient(), new Patient()));
        when(mockRepository.findAll(PageRequest.of(0, 1))).thenReturn(patients);
        final Page<Patient> result = patientServiceUnderTest.findAll(0, 1);
        assertNotNull(result.getContent());
    }

    @Test
    void testFindAll_PatientRepositoryReturnsNoItems() {
        when(mockRepository.findAll(PageRequest.of(0, 1))).thenReturn(new PageImpl<>(Collections.emptyList()));
        final Page<Patient> result = patientServiceUnderTest.findAll(0, 1);
        assertEquals(result.getContent(), List.of());
    }

    @Test
    void testCreate() {
        Patient test = spy();
        test.setUser(spy(User.class));

        when(mockUserRepository.save(any())).thenReturn(new User());
        when(mockRepository.save(test)).thenReturn(patient);

        final Patient result = patientServiceUnderTest.create(test);
        assertEquals(patient, result);
        verify(mockUserRepository).save(new User());
    }

    @Test
    void testUpdate() {
        Patient test = spy();
        test.setUser(spy(User.class));

        when(mockUserRepository.save(any())).thenReturn(new User());
        when(mockRepository.save(test)).thenReturn(patient);

        final Patient result = patientServiceUnderTest.create(test);
        assertEquals(patient, result);
        verify(mockUserRepository).save(new User());
    }

    @Test
    void testDelete() {
        patientServiceUnderTest.delete(spy(Patient.class));
        verify(mockRepository).delete(any());
    }

    @Test
    void testDeleteByChatID() {
        patientServiceUnderTest.deleteByChatID(anyLong());
        verify(mockRepository).deletePatientByChatID(anyLong());
    }

    @Test
    void testDeleteByID() {
        patientServiceUnderTest.deleteByID(anyLong());
        verify(mockRepository).deleteById(anyLong());
    }

    @Test
    void testFindByChatID() {
        when(mockRepository.findPatientByChatID(0L)).thenReturn(Optional.of(patient));
        final Optional<Patient> result = patientServiceUnderTest.findByChatID(0L);
        assertTrue(result.isPresent());
        assertEquals(patient, result.get());
    }

    @Test
    void testFindByChatID_PatientRepositoryReturnsAbsent() {
        when(mockRepository.findPatientByChatID(0L)).thenReturn(Optional.empty());
        final Optional<Patient> result = patientServiceUnderTest.findByChatID(0L);
        assertEquals(Optional.empty(), result);
    }

    @Test
    void testFindByID() {
        when(mockRepository.findById(anyLong())).thenReturn(Optional.of(patient));
        final Optional<Patient> result = patientServiceUnderTest.findByID(anyLong());
        assertTrue(result.isPresent());
        assertEquals(patient, result.get());
    }

    @Test
    void testFindByID_PatientRepositoryReturnsAbsent() {
        when(mockRepository.findById(anyLong())).thenReturn(Optional.empty());
        final Optional<Patient> result = patientServiceUnderTest.findByID(anyLong());
        assertEquals(Optional.empty(), result);
    }

    @Test
    void testValidateBeforeSave() {
        Patient optionalPatient = new Patient("test-token", 1, "test", "test", "test", PatientState.STABLE, 13, "-");
        optionalPatient.setUser(new User("test", "test", "test"));
        assertTrue(patientServiceUnderTest.validateBeforeSave(optionalPatient));
    }

    @Test
    void testValidateBeforeSaveFailure() {
        assertFalse(patientServiceUnderTest.validateBeforeSave(patient));
    }
}
