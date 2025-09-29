package com.hospital.management.common.exceptions;

import java.util.List;
import java.util.ArrayList;

/**
 * Custom exception for validation errors
 */
public class ValidationException extends Exception {

    private final String field;
    private final Object invalidValue;
    private final List<String> validationErrors;

    public ValidationException(String message) {
        super(message);
        this.field = "Unknown";
        this.invalidValue = null;
        this.validationErrors = new ArrayList<>();
        this.validationErrors.add(message);
    }

    public ValidationException(String message, String field) {
        super(message);
        this.field = field;
        this.invalidValue = null;
        this.validationErrors = new ArrayList<>();
        this.validationErrors.add(message);
    }

    public ValidationException(String message, String field, Object invalidValue) {
        super(message);
        this.field = field;
        this.invalidValue = invalidValue;
        this.validationErrors = new ArrayList<>();
        this.validationErrors.add(message);
    }

    public ValidationException(List<String> validationErrors) {
        super("Multiple validation errors occurred");
        this.field = "Multiple";
        this.invalidValue = null;
        this.validationErrors = new ArrayList<>(validationErrors);
    }

    public String getField() {
        return field;
    }

    public Object getInvalidValue() {
        return invalidValue;
    }

    public List<String> getValidationErrors() {
        return new ArrayList<>(validationErrors);
    }

    public void addValidationError(String error) {
        this.validationErrors.add(error);
    }

    @Override
    public String toString() {
        if (validationErrors.size() == 1) {
            return String.format("ValidationException [field=%s, value=%s]: %s",
                    field, invalidValue, validationErrors.get(0));
        } else {
            return String.format("ValidationException: Multiple errors - %s",
                    String.join(", ", validationErrors));
        }
    }
}
