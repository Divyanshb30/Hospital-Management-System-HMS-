package com.hospital.management.common.enums;

/**
 * Appointment status enumeration
 */
public enum AppointmentStatus {
    SCHEDULED("Scheduled", "Appointment is scheduled"),
    IN_PROGRESS("In Progress", "Appointment is currently in progress"),
    COMPLETED("Completed", "Appointment has been completed"),
    CANCELLED("Cancelled", "Appointment has been cancelled"),
    NO_SHOW("No Show", "Patient did not show up");

    private final String displayName;
    private final String description;

    AppointmentStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}
