package org.patientbot.patienttelegrambot.entity;

import lombok.Getter;

@Getter
public enum TelegramBotResponses {
    BAD_CREDS("Patient with this token was not found. Try again"),
    SYNTAX_ERROR("Unsupported syntax format. Try again"),
    AUTH_PASSED("Authentication successfully passed"),
    PATIENT_NOT_FOUNT("Patient with such ID not found. Try again"),

    PERMISSION_DENIED_BECAUSE_OF_AUTHENTICATION("You don't have permission for these resource because of authentication. To get access write '/authenticate' and pass you token.");

    private final String description;

    TelegramBotResponses(String description) {
        this.description = description;
    }

}

