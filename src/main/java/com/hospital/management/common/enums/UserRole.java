package com.hospital.management.common.enums;

/**
 * Enum representing different user roles in the hospital management system
 */
public enum UserRole {
    PATIENT("Patient", "Can book appointments and view medical records"),
    DOCTOR("Doctor", "Can manage appointments and update patient records"),
    ADMIN("Administrator", "Can manage all users and system settings");

    private final String displayName;
    private final String description;

    UserRole(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
