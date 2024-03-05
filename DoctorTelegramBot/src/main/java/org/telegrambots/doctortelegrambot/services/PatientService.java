package org.telegrambots.doctortelegrambot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegrambots.doctortelegrambot.entities.Patient;
import org.telegrambots.doctortelegrambot.entities.PatientState;
import org.telegrambots.doctortelegrambot.repositories.EntityDAO;
import org.telegrambots.doctortelegrambot.repositories.PatientRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientService extends EntityDAO<Patient, Integer> {

    private final PatientRepository repository;

    @Override
    public List<Patient> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Patient> findByID(Integer id) {
        return repository.findById(id);
    }

    @Override
    public Patient create(Object object) {
        return repository.save((Patient) object);
    }

    @Override
    public Patient update(Object object) {
        return repository.save((Patient) object);
    }

    @Override
    public void delete(Object object) {
        repository.delete((Patient) object);
    }

    @Override
    public void deleteByID(Integer id) {
        repository.deleteById(id);
    }

    public boolean validatePatientBeforeSave(Patient patient) {
        if (patient.getName() == null || patient.getName().isEmpty()) return false;
        if (patient.getSecondName() == null || patient.getSecondName().isEmpty()) return false;
        if (patient.getDisease() == null || patient.getDisease().isEmpty()) return false;
        if (patient.getPatientState() == null || Arrays.stream(PatientState
                        .values())
                .noneMatch(patientState -> patientState.equals(patient.getPatientState())))
            return false;
        if (patient.getChamberNumber() < 0 || patient.getChamberNumber() > 1000) return false;
        return true;
    }
}
