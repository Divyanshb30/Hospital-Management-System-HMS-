package com.hospital.management.common.constants;

/**
 * User messages and error texts used throughout the application
 */
public final class MessageConstants {

    // Success Messages
    public static final String SUCCESS_PATIENT_CREATED = "✅ Patient registered successfully with ID: %d";
    public static final String SUCCESS_DOCTOR_CREATED = "✅ Doctor added successfully with ID: %d";
    public static final String SUCCESS_RECORD_UPDATED = "✅ Record updated successfully";
    public static final String SUCCESS_RECORD_DELETED = "✅ Record deleted successfully";

    // Error Messages
    public static final String ERROR_PATIENT_NOT_FOUND = "❌ Patient not found with ID: %d";
    public static final String ERROR_DOCTOR_NOT_FOUND = "❌ Doctor not found with ID: %d";
    public static final String ERROR_INVALID_INPUT = "❌ Invalid input provided: %s";
    public static final String ERROR_DATABASE_CONNECTION = "❌ Database connection failed";
    public static final String ERROR_OPERATION_FAILED = "❌ Operation failed: %s";

    // Validation Messages
    public static final String VALIDATION_REQUIRED_FIELD = "%s is required";
    public static final String VALIDATION_INVALID_EMAIL = "Invalid email format";
    public static final String VALIDATION_INVALID_PHONE = "Invalid phone number format";
    public static final String VALIDATION_INVALID_DATE = "Invalid date format";

    // Menu Messages
    public static final String MENU_PATIENT_MANAGEMENT = "👤 Patient Management";
    public static final String MENU_DOCTOR_MANAGEMENT = "👩‍⚕️ Doctor Management";
    public static final String MENU_APPOINTMENT_MANAGEMENT = "📅 Appointment Management";

    private MessageConstants() {
        // Prevent instantiation
    }
}
