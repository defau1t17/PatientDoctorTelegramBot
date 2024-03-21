package org.telegrambots.doctortelegrambot.entities;

import java.util.Arrays;

public enum ChatStates {
    DEFAULT(""),
    WAITING_FOR_TOKEN("/authenticate"),
    WAITING_FOR_NAME("/new_patient"), WAITING_FOR_SECONDNAME("/new_patient"), WAITING_FOR_DISEASE("/new_patient"), WAITING_FOR_PATIENT_STATE("/new_patient"), WAITING_FOR_CHAMBER_NUMBER("/new_patient"), WAITING_FOR_DESCRIPTION("/new_patient"),
    WAITING_FOR_PATIENT_ID("/patient"), WAITING_FOR_PREVIOUS_OR_NEXT_COMMAND("/patients");

    private final String commandReference;

    ChatStates(String reference) {
        this.commandReference = reference;
    }

    public String getCommandReference() {
        return this.commandReference;
    }


    public ChatStates move(String commandReference) {
        int nextIndex = this.ordinal() + 1;
        if (this == DEFAULT) {
            return Arrays.stream(values())
                    .filter(refer -> commandReference
                            .equals(refer.getCommandReference()))
                    .findFirst().orElse(null);
        } else if (nextIndex < values().length && values()[nextIndex].getCommandReference().equals(this.commandReference)) {
            return values()[nextIndex];
        } else {
            return DEFAULT;
        }
    }

}
