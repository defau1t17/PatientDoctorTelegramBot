package org.hospital.hospitalservice.services;

import lombok.RequiredArgsConstructor;
import org.hospital.hospitalservice.entities.*;
import org.hospital.hospitalservice.repositories.DoctorRepository;
import org.hospital.hospitalservice.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DoctorService extends ServiceDAO<Doctor, Long> {

    private final DoctorRepository repository;

    private final PatientService patientService;

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Doctor> findAll(int pageNumber, int pageSize) {
        return repository.findAll(PageRequest.of(pageNumber, pageSize));
    }

    @Override
    @Transactional

    public Doctor create(Doctor entity) {
        userRepository.save(entity.getUser());
        return repository.save(entity);
    }

    @Override
    @Transactional

    public Doctor update(Doctor entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional

    public void delete(Doctor entity) {
        repository.delete(entity);
    }

    @Override
    @Transactional

    public void deleteByChatID(Long chatID) {
        repository.deleteDoctorByChatID(chatID);
    }

    @Override
    @Transactional

    public void deleteByID(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)

    public Optional<Doctor> findByChatID(Long chatID) {
        return repository.findDoctorByChatID(chatID);
    }

    @Override
    @Transactional(readOnly = true)

    public Optional<Doctor> findByID(Long id) {
        return repository.findById(id);
    }

    @Override
    public boolean validateBeforeSave(Doctor entity) {
        if (entity.getUser().getName() == null || entity.getUser().getName().isEmpty()) return false;
        if (entity.getUser().getSecondName() == null || entity.getUser().getSecondName().isEmpty()) return false;
        if (entity.getDoctorShift() == null || Arrays.stream(DoctorShift
                        .values())
                .noneMatch(doctorShift -> doctorShift.equals(entity.getDoctorShift()))) return false;
        if (entity.getDoctorPosition() == null || Arrays.stream(DoctorPosition
                        .values())
                .noneMatch(doctorPosition -> doctorPosition.equals(entity.getDoctorPosition()))) return false;
        if (entity.getWorkroom() < 0 || entity.getWorkroom() > 1000) return false;
        return true;
    }

    public Doctor changeDoctorShiftStatus(Doctor doctor) {
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

    public boolean assignPatientToDoctor(long patientID, long doctorID) {
        if (validateEntitiesBeforeOperation(patientID, doctorID)) {
            Doctor doctor = findByID(doctorID).get();
            doctor.addPatient(patientService.findByID(patientID).get());
            return update(doctor) != null;
        }
        return false;
    }

    public boolean unhookPatientFromDoctor(long patientID, long doctorID) {
        if (validateEntitiesBeforeOperation(patientID, doctorID)) {
            Doctor doctor = findByID(doctorID).get();
            doctor.removePatient(patientService.findByID(patientID).get());
            return update(doctor) != null;
        }
        return false;
    }

    private boolean validateEntitiesBeforeOperation(long patientID, long doctorID) {
        Optional<Doctor> optionalDoctor = findByID(doctorID);
        Optional<Patient> optionalPatient = patientService.findByID(patientID);
        return optionalPatient.isPresent() && optionalDoctor.isPresent();
    }

}
