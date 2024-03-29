package org.patientbot.patienttelegrambot.entity;

public enum ChatStates {
    DEFAULT(""),
    WAITING_FOR_TOKEN("/authenticate"),
    WAITING_FOR_NAME("/new_patient"), WAITING_FOR_SECONDNAME("/new_patient"), WAITING_FOR_DISEASE("/new_patient"), WAITING_FOR_PATIENT_STATE("/new_patient"), WAITING_FOR_CHAMBER_NUMBER("/new_patient"), WAITING_FOR_DESCRIPTION("/new_patient"),
    WAITING_FOR_PATIENT_ID("/patient"), WAITING_FOR_PREVIOUS_OR_NEXT_COMMAND("/patients");


    private final String description;

    ChatStates(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
