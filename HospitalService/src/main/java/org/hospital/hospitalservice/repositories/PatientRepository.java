package org.hospital.hospitalservice.repositories;

import org.hospital.hospitalservice.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    @Query(value = "SELECT patient FROM Patient patient WHERE patient.user.chatID = ?1")
    Optional<Patient> findPatientByChatID(long chatID);
    @Query(value = "DELETE FROM Patient patient WHERE patient.user.chatID = ?1")
    void deletePatientByChatID(long chatID);
}
