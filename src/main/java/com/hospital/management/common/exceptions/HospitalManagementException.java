package com.hospital.management.common.exceptions;

/**
 * Base exception class for all hospital management system exceptions
 */
public class HospitalManagementException extends RuntimeException {

    private final String errorCode;

    public HospitalManagementException(String message) {
        super(message);
        this.errorCode = "HMS_ERROR";
    }

    public HospitalManagementException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public HospitalManagementException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "HMS_ERROR";
    }

    public HospitalManagementException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
