package org.telegrambots.doctortelegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.telegrambots.doctortelegrambot.entity.Doctor;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Integer> {
}
