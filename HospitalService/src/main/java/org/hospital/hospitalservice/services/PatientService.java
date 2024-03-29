package org.hospital.hospitalservice.services;

import lombok.RequiredArgsConstructor;
import org.hospital.hospitalservice.entities.Patient;
import org.hospital.hospitalservice.entities.PatientState;
import org.hospital.hospitalservice.repositories.PatientRepository;
import org.hospital.hospitalservice.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientService extends ServiceDAO<Patient, Long> {
    private final PatientRepository repository;

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Patient> findAll(int pageNumber, int pageSize) {
        return repository.findAll(PageRequest.of(pageNumber, pageSize));
    }

    @Override
    @Transactional
    public Patient create(Patient entity) {
        userRepository.save(entity.getUser());
        return repository.save(entity);
    }

    @Override
    @Transactional
    public Patient update(Patient entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional
    public void delete(Patient entity) {
        repository.delete(entity);
    }

    @Override
    @Transactional

    public void deleteByChatID(Long chatID) {
        repository.deletePatientByChatID(chatID);
    }

    @Override
    @Transactional
    public void deleteByID(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Patient> findByChatID(Long chatID) {
        return repository.findPatientByChatID(chatID);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Patient> findByID(Long id) {
        return repository.findById(id);
    }

    @Override
    public boolean validateBeforeSave(Patient entity) {
        if (entity.getUser().getName() == null || entity.getUser().getName().isEmpty()) return false;
        if (entity.getUser().getSecondName() == null || entity.getUser().getSecondName().isEmpty()) return false;
        if (entity.getDisease() == null || entity.getDisease().isEmpty()) return false;
        if (entity.getPatientState() == null || Arrays.stream(PatientState
                        .values())
                .noneMatch(patientState -> patientState.equals(entity.getPatientState())))
            return false;
        if (entity.getChamberNumber() < 0 || entity.getChamberNumber() > 1000) return false;
        return true;
    }
}
