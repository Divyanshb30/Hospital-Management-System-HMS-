package com.hospital.management.common.utils;

import com.hospital.management.common.exceptions.ValidationException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Utility class for input validation with Java 8 features
 */
public final class InputValidator {

    // Private constructor to prevent instantiation
    private InputValidator() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // Regex patterns for validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[+]?[1-9]\\d{1,14}$|^\\d{10}$"
    );

    private static final Pattern NAME_PATTERN = Pattern.compile(
            "^[A-Za-z\\s]{2,50}$"
    );

    /**
     * Validates if a string is not null and not empty
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Validates email format
     */
    public static boolean isValidEmail(String email) {
        return isNotEmpty(email) && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validates phone number format
     */
    public static boolean isValidPhone(String phone) {
        return isNotEmpty(phone) && PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * Validates name format (only letters and spaces, 2-50 characters)
     */
    public static boolean isValidName(String name) {
        return isNotEmpty(name) && NAME_PATTERN.matcher(name.trim()).matches();
    }

    /**
     * Validates if a string meets minimum length requirement
     */
    public static boolean hasMinLength(String value, int minLength) {
        return isNotEmpty(value) && value.trim().length() >= minLength;
    }

    /**
     * Validates if a string is within max length
     */
    public static boolean hasMaxLength(String value, int maxLength) {
        return value == null || value.trim().length() <= maxLength;
    }

    /**
     * Validates username format (alphanumeric and underscore, 3-20 characters)
     */
    public static boolean isValidUsername(String username) {
        if (!isNotEmpty(username)) return false;
        String trimmed = username.trim();
        return trimmed.length() >= 3 && trimmed.length() <= 20 &&
                trimmed.matches("^[a-zA-Z0-9_]+$");
    }

    /**
     * Validates password strength
     */
    public static boolean isValidPassword(String password) {
        if (!hasMinLength(password, 8)) return false;

        // Check for at least one uppercase, lowercase, digit, and special character
        return password.matches(".*[A-Z].*") &&
                password.matches(".*[a-z].*") &&
                password.matches(".*\\d.*") &&
                password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
    }

    /**
     * Validates age within reasonable range
     */
    public static boolean isValidAge(int age) {
        return age >= 0 && age <= 150;
    }

    /**
     * Validates date format and parsing
     */
    public static boolean isValidDate(String dateStr, String format) {
        if (!isNotEmpty(dateStr)) return false;

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            LocalDate.parse(dateStr.trim(), formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Validates that date is not in the past
     */
    public static boolean isNotPastDate(LocalDate date) {
        return date != null && !date.isBefore(LocalDate.now());
    }

    /**
     * Validates that datetime is not in the past
     */
    public static boolean isNotPastDateTime(LocalDateTime dateTime) {
        return dateTime != null && !dateTime.isBefore(LocalDateTime.now());
    }

    /**
     * Validates numeric input within range
     */
    public static boolean isWithinRange(double value, double min, double max) {
        return value >= min && value <= max;
    }

    /**
     * Comprehensive validation method with exception throwing
     */
    public static void validateRequired(String value, String fieldName) throws ValidationException {
        if (!isNotEmpty(value)) {
            throw new ValidationException(fieldName + " is required", fieldName, value);
        }
    }

    /**
     * Validate email with exception throwing
     */
    public static void validateEmail(String email, String fieldName) throws ValidationException {
        validateRequired(email, fieldName);
        if (!isValidEmail(email)) {
            throw new ValidationException("Invalid email format", fieldName, email);
        }
    }

    /**
     * Validate phone with exception throwing
     */
    public static void validatePhone(String phone, String fieldName) throws ValidationException {
        validateRequired(phone, fieldName);
        if (!isValidPhone(phone)) {
            throw new ValidationException("Invalid phone number format", fieldName, phone);
        }
    }

    /**
     * Validate name with exception throwing
     */
    public static void validateName(String name, String fieldName) throws ValidationException {
        validateRequired(name, fieldName);
        if (!isValidName(name)) {
            throw new ValidationException("Name should contain only letters and spaces (2-50 characters)", fieldName, name);
        }
    }
}
