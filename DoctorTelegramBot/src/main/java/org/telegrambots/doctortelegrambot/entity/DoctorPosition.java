package org.telegrambots.doctortelegrambot.entity;

public enum DoctorPosition {
    CHIEF_PHYSICIAN("Chief Physician"),
    HEAD_OF_DEPARTMENT("Head of Department"),
    GENERAL_PRACTITIONER("General Practitioner"),
    SURGEON("Surgeon"),
    PEDIATRICIAN("Pediatrician"),
    GYNECOLOGIST("Gynecologist"),
    CARDIOLOGIST("Cardiologist"),
    RADIOLOGIST("Radiologist"),
    ANESTHESIOLOGIST("Anesthesiologist"),
    NURSE("Nurse"),
    PARAMEDIC("Paramedic"),
    LAB_TECHNICIAN("Lab Technician");

    private final String position;

    DoctorPosition(String position) {
        this.position = position;
    }

    public String getPosition() {
        return position;
    }
}
