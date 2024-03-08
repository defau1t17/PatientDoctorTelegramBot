package org.telegrambots.doctortelegrambot.entities;

public enum ChatStates {
    DEFAULT, WAITING_FOR_NAME, WAITING_FOR_SECONDNAME, WAITING_FOR_DISEASE, WAITING_FOR_PATIENT_STATE, WAITING_FOR_CHAMBER_NUMBER, WAITING_FOR_DESCRIPTION;
    public ChatStates next() {
        int nextIndex = this.ordinal() + 1;
        if (nextIndex < values().length) {
            return values()[nextIndex];
        } else {
            return DEFAULT;
        }
    }
}
