package com.hospital.management.common.exceptions;

/**
 * Custom exception for authentication and authorization issues
 */
public class AuthenticationException extends Exception {

    private final String username;
    private final String attemptType;

    public AuthenticationException(String message) {
        super(message);
        this.username = "Unknown";
        this.attemptType = "Login";
    }

    public AuthenticationException(String message, String username) {
        super(message);
        this.username = username;
        this.attemptType = "Login";
    }

    public AuthenticationException(String message, String username, String attemptType) {
        super(message);
        this.username = username;
        this.attemptType = attemptType;
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
        this.username = "Unknown";
        this.attemptType = "Login";
    }

    public String getUsername() {
        return username;
    }

    public String getAttemptType() {
        return attemptType;
    }

    @Override
    public String toString() {
        return String.format("AuthenticationException [user=%s, type=%s]: %s",
                username, attemptType, getMessage());
    }
}
