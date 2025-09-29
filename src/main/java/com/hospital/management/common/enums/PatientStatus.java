package com.hospital.management.common.enums;

/**
 * Patient status enumeration
 */
public enum PatientStatus {
    ACTIVE("Active", "Patient is currently active"),
    DISCHARGED("Discharged", "Patient has been discharged"),
    CRITICAL("Critical", "Patient is in critical condition"),
    DECEASED("Deceased", "Patient is deceased"),
    TRANSFERRED("Transferred", "Patient has been transferred");

    private final String displayName;
    private final String description;

    PatientStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }

    public static PatientStatus fromString(String status) {
        for (PatientStatus ps : values()) {
            if (ps.name().equalsIgnoreCase(status)) {
                return ps;
            }
        }
        return ACTIVE; // Default
    }
}
