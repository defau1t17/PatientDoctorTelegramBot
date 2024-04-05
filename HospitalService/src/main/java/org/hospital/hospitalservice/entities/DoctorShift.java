package org.hospital.hospitalservice.entities;

public enum DoctorShift {
    DAILY_SHIFT("9:00-18:00"),
    NIGHT_SHIFT("18:00-6:00");

    private final String workTime;

    DoctorShift(String workTime) {
        this.workTime = workTime;
    }

    public String getWorkTime() {
        return workTime;
    }
}
