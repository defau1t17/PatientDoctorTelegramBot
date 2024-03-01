package org.telegrambots.doctortelegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.telegrambots.doctortelegrambot.entity.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Integer> {
}
