package org.patientbot.patienttelegrambot.entity;

import lombok.Getter;

@Getter
public enum ChatStates {
    DEFAULT(""),
    WAITING_FOR_TOKEN("/authenticate"),
    WAITING_FOR_DOCTOR_ID("/doctor_id"), WAITING_FOR_PREVIOUS_OR_NEXT_COMMAND_DOCTORS("/doctors"),
    CANCEL("/cancel");


    private final String description;

    ChatStates(String description) {
        this.description = description;
    }

}
