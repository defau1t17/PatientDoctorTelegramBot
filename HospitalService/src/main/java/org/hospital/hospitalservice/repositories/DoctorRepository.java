package org.hospital.hospitalservice.repositories;

import org.hospital.hospitalservice.entities.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    @Query("DELETE FROM Doctor doctor where doctor.user.chatID = ?1")
    void deleteDoctorByChatID(Long chatID);

    @Query(value = "SELECT doctor FROM Doctor doctor WHERE doctor.user.chatID = ?1")
    Optional<Doctor> findDoctorByChatID(long chatID);
}
