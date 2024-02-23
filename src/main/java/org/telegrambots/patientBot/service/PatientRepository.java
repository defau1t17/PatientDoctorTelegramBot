package org.telegrambots.patientBot.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.telegrambots.patientBot.entity.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Integer> {
}
