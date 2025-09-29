package com.hospital.management.common.exceptions;

/**
 * Custom exception for database-related operations
 */
public class DatabaseException extends Exception {

    private final String operation;
    private final int errorCode;

    public DatabaseException(String message) {
        super(message);
        this.operation = "Unknown";
        this.errorCode = -1;
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
        this.operation = "Unknown";
        this.errorCode = -1;
    }

    public DatabaseException(String message, String operation) {
        super(message);
        this.operation = operation;
        this.errorCode = -1;
    }

    public DatabaseException(String message, String operation, int errorCode) {
        super(message);
        this.operation = operation;
        this.errorCode = errorCode;
    }

    public DatabaseException(String message, String operation, Throwable cause) {
        super(message, cause);
        this.operation = operation;
        this.errorCode = -1;
    }

    public String getOperation() {
        return operation;
    }

    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return String.format("DatabaseException [operation=%s, errorCode=%d]: %s",
                operation, errorCode, getMessage());
    }
}
