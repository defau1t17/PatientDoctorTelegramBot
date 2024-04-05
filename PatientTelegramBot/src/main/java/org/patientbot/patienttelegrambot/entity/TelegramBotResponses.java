package org.patientbot.patienttelegrambot.entity;

import lombok.Getter;

@Getter
public enum TelegramBotResponses {
    BAD_CREDS("Patient with this token was not found. Try again"),
    INPUT_TOKEN_DESCRIPTION("Please, write your personal access token \n(Avoid '@,|,/,!,#,$,%,^,&,*,(,),{,},[,]' symbols)\n"),
    INPUT_DOCTOR_ID("Please, inter doctor ID below : "),
    NO_DOCTOR_WITH_SUCH_ID("Unfortunately, doctor with such ID not found, please try again by pressing on ---> '/doctor_id'"),
    SYNTAX_ERROR("Unsupported syntax format. Try again"),
    AUTH_PASSED("Authentication successfully passed!"),
    PATIENT_NOT_FOUNT("Patient with such ID not found.\n You may try again by pressing -> '/doctor_id' "),
    PERMISSION_DENIED_BECAUSE_OF_AUTHENTICATION("You don't have permission for these resource because you are not authenticated.\n Press on -> '/authenticate' and provide your token."),
    SOME_ERROR("Unknown error happened.\n Please click -> '/cancel' and start again :D ");

    private final String description;

    TelegramBotResponses(String description) {
        this.description = description;
    }

}

