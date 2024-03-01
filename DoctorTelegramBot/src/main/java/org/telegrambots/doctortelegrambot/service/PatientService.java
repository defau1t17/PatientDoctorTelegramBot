package org.telegrambots.doctortelegrambot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegrambots.doctortelegrambot.entity.Patient;
import org.telegrambots.doctortelegrambot.repository.EntityDAO;
import org.telegrambots.doctortelegrambot.repository.PatientRepository;

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
}
