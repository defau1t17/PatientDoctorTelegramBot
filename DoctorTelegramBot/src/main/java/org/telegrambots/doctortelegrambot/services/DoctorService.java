package org.telegrambots.doctortelegrambot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegrambots.doctortelegrambot.entities.Doctor;
import org.telegrambots.doctortelegrambot.entities.Patient;
import org.telegrambots.doctortelegrambot.entities.ShiftStatus;
import org.telegrambots.doctortelegrambot.repositories.DoctorRepository;
import org.telegrambots.doctortelegrambot.repositories.EntityDAO;

import javax.print.Doc;
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

    public Optional<Doctor> findDoctorByChatID(int chatID) {
        return repository.findDoctorByChatID(chatID);
    }

    public boolean addNewPatientIntoDoctorResponsibleList(int patientID, int doctorID) {
        if (validatePatientBefore(patientID, doctorID)) {
            Doctor doctor = findByID(doctorID).get();
            doctor.addPatient(patientService.findByID(patientID).get());
            return update(doctor) != null;
        }
        return false;
    }

    public boolean removePatientIntoDoctorResponsibleList(int patientID, int doctorID) {
        if (validatePatientBefore(patientID, doctorID)) {
            Doctor doctor = findByID(doctorID).get();
            doctor.removePatient(patientService.findByID(patientID).get());
            return update(doctor) != null;
        }
        return false;
    }

    private boolean validatePatientBefore(int patientID, int doctorID) {
        Optional<Doctor> optionalDoctor = findByID(doctorID);
        Optional<Patient> optionalPatient = patientService.findByID(patientID);
        return optionalPatient.isPresent() && optionalDoctor.isPresent();
    }

    public Doctor doctorShiftManipulation(int doctorID) {
        if (findDoctorByChatID(doctorID).isPresent()) {
            Doctor doctor = findDoctorByChatID(doctorID).get();
            switch (doctor.getShiftStatus()) {
                case CLOSED:
                    doctor.setShiftStatus(ShiftStatus.OPENED);
                    break;
                case OPENED:
                    doctor.setShiftStatus(ShiftStatus.CLOSED);
                    break;
                default:
                    //some logs
            }
            return update(doctor);
        }
        return null;
    }


}
