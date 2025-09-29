package com.hospital.management.common.utils;

import com.hospital.management.common.constants.ValidationConstants;
import java.util.regex.Pattern;

/**
 * Utility class for input validation using try-with-resources pattern
 */
public final class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(ValidationConstants.EMAIL_PATTERN);
    private static final Pattern PHONE_PATTERN = Pattern.compile(ValidationConstants.PHONE_PATTERN);
    private static final Pattern NAME_PATTERN = Pattern.compile(ValidationConstants.NAME_PATTERN);

    private ValidationUtils() {
        // Prevent instantiation
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name).matches() &&
                name.length() <= ValidationConstants.MAX_NAME_LENGTH;
    }

    public static boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }

    public static boolean isWithinLength(String str, int maxLength) {
        return str == null || str.length() <= maxLength;
    }

    public static boolean isValidAge(int age) {
        return age >= ValidationConstants.MIN_AGE && age <= ValidationConstants.MAX_AGE;
    }

    public static void requireNonNull(Object obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void requireNotBlank(String str, String fieldName) {
        if (!isNotBlank(str)) {
            throw new IllegalArgumentException(String.format(
                    ValidationConstants.VALIDATION_REQUIRED_FIELD, fieldName));
        }
    }
}
