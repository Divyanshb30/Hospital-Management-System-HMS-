package com.hospital.management.common.enums;

/**
 * Enum representing appointment status in the system
 */
public enum AppointmentStatus {
    SCHEDULED("Scheduled", "Appointment is scheduled and confirmed"),
    IN_PROGRESS("In Progress", "Appointment is currently ongoing"),
    COMPLETED("Completed", "Appointment has been completed successfully"),
    CANCELLED("Cancelled", "Appointment has been cancelled"),
    NO_SHOW("No Show", "Patient did not show up for appointment"),
    RESCHEDULED("Rescheduled", "Appointment has been moved to different time");

    private final String displayName;
    private final String description;

    AppointmentStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == SCHEDULED || this == IN_PROGRESS;
    }

    public boolean isCompleted() {
        return this == COMPLETED || this == CANCELLED || this == NO_SHOW;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
