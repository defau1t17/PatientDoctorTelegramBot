package org.telegrambots.doctortelegrambot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.telegrambots.doctortelegrambot.entities.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Integer> {
}
