package com.hospital.management.common.constants;

/**
 * Validation patterns and limits used across the application
 */
public final class ValidationConstants {

    // Validation Messages - ADD THIS SECTION
    public static final String VALIDATION_REQUIRED_FIELD = "%s is required";
    public static final String VALIDATION_INVALID_EMAIL = "Invalid email format";
    public static final String VALIDATION_INVALID_PHONE = "Invalid phone number format";
    public static final String VALIDATION_INVALID_DATE = "Invalid date format";
    public static final String VALIDATION_FIELD_TOO_LONG = "%s cannot exceed %d characters";
    public static final String VALIDATION_INVALID_RANGE = "%s must be between %d and %d";

    // String Length Limits
    public static final int MAX_NAME_LENGTH = 100;
    public static final int MAX_EMAIL_LENGTH = 255;
    public static final int MAX_PHONE_LENGTH = 20;
    public static final int MAX_ADDRESS_LENGTH = 500;
    public static final int MIN_NAME_LENGTH = 2;

    // Regex Patterns
    public static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    public static final String PHONE_PATTERN = "^[+]?[0-9]{10,15}$";
    public static final String NAME_PATTERN = "^[A-Za-z\\s.'-]{2,}$";

    // Numeric Limits
    public static final int MIN_AGE = 0;
    public static final int MAX_AGE = 150;
    public static final double MIN_WEIGHT = 0.5; // kg
    public static final double MAX_WEIGHT = 1000.0; // kg
    public static final double MIN_HEIGHT = 30.0; // cm
    public static final double MAX_HEIGHT = 300.0; // cm

    private ValidationConstants() {
        // Prevent instantiation
    }
}
