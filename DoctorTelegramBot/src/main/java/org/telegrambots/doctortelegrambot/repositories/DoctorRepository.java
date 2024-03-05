package org.telegrambots.doctortelegrambot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.telegrambots.doctortelegrambot.entities.Doctor;

import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Integer> {
    @Query(value = "SELECT d FROM Doctor d WHERE d.personalToken.chatID = ?1 ")
    Optional<Doctor> findDoctorByChatID(int chatID);
}
