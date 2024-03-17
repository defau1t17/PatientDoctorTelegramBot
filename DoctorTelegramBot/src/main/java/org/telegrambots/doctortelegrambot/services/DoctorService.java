package org.telegrambots.doctortelegrambot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegrambots.doctortelegrambot.entities.*;
import org.telegrambots.doctortelegrambot.repositories.DoctorRepository;
import org.telegrambots.doctortelegrambot.repositories.EntityDAO;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DoctorService extends EntityDAO<Doctor, Integer> {

    private final DoctorRepository repository;

    private final PatientService patientService;

    @Override
    public List<Doctor> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Doctor> findByID(Integer id) {
        return repository.findById(id);
    }

    @Override
    public Doctor create(Object object) {
        return repository.save((Doctor) object);
    }

    @Override
    public Doctor update(Object object) {
        return repository.save((Doctor) object);
    }

    @Override
    public void delete(Object object) {
        repository.delete((Doctor) object);
    }

    @Override
    public void deleteByID(Integer id) {
        repository.deleteById(id);
    }

    public Optional<Doctor> findDoctorByChatID(long chatID) {
        return repository.findDoctorByChatID(chatID);
    }

    public boolean addNewPatientIntoDoctorResponsibleList(int patientID, int doctorID) {
        if (validateEntitiesBeforeOperation(patientID, doctorID)) {
            Doctor doctor = findByID(doctorID).get();
            doctor.addPatient(patientService.findByID(patientID).get());
            return update(doctor) != null;
        }
        return false;
    }

    public boolean removePatientFromDoctorResponsibleList(int patientID, int doctorID) {
        if (validateEntitiesBeforeOperation(patientID, doctorID)) {
            Doctor doctor = findByID(doctorID).get();
            doctor.removePatient(patientService.findByID(patientID).get());
            return update(doctor) != null;
        }
        return false;
    }

    private boolean validateEntitiesBeforeOperation(int patientID, int doctorID) {
        Optional<Doctor> optionalDoctor = findByID(doctorID);
        Optional<Patient> optionalPatient = patientService.findByID(patientID);
        return optionalPatient.isPresent() && optionalDoctor.isPresent();
    }

    public Doctor doctorShiftManipulation(long doctorChatID) {
        if (findDoctorByChatID(doctorChatID).isPresent()) {
            Doctor doctor = findDoctorByChatID(doctorChatID).get();
            switch (doctor.getShiftStatus()) {
                case CLOSED:
                    doctor.setShiftStatus(ShiftStatus.OPENED);
                    break;
                case OPENED:
                    doctor.setShiftStatus(ShiftStatus.CLOSED);
                    break;
            }
            return update(doctor);
        }
        return null;
    }

    public boolean validateDoctorBeforeSave(Doctor doctor) {
        if (doctor.getName() == null || doctor.getName().isEmpty()) return false;
        if (doctor.getSecondName() == null || doctor.getSecondName().isEmpty()) return false;
        if (doctor.getDoctorShift() == null || Arrays.stream(DoctorShift
                        .values())
                .noneMatch(doctorShift -> doctorShift.equals(doctor.getDoctorShift()))) return false;
        if (doctor.getDoctorPosition() == null || Arrays.stream(DoctorPosition
                        .values())
                .noneMatch(doctorPosition -> doctorPosition.equals(doctor.getDoctorPosition()))) return false;
        if (doctor.getWorkroom() < 0 || doctor.getWorkroom() > 1000) return false;
        if (doctor.getShiftStatus() == null || Arrays.stream(ShiftStatus
                        .values())
                .noneMatch(shiftStatus -> shiftStatus.equals(doctor.getShiftStatus()))) return false;
        return true;
    }


}
