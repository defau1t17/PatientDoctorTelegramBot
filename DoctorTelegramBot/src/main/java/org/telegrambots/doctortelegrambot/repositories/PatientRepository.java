package org.telegrambots.doctortelegrambot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.telegrambots.doctortelegrambot.entities.Patient;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Integer> {
    @Query(value = "SELECT p FROM Patient p WHERE p.personalToken.chatID = ?1")
    Optional<Patient> findPatientByChatID(int chatID);
}
