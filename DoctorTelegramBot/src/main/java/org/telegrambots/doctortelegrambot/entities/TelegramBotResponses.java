package org.telegrambots.doctortelegrambot.entities;

public enum TelegramBotResponses {
    BAD_CREDS("Patient with this token was not found. Try again"),
    SYNTAX_ERROR("Unsupported syntax format. Try again"),
    AUTH_PASSED("Authentication successfully passed"),

    PATIENT_NOT_FOUNT("Patient with such ID not found. Try again");
    private final String description;

    TelegramBotResponses(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

