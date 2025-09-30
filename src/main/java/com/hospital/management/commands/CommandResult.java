package com.hospital.management.commands;

/**
 * Result wrapper for command execution
 * Contains success status, data, and messages
 */
public class CommandResult {
    private final boolean success;
    private final String message;
    private final Object data;
    private final Exception exception;

    // Success constructors
    public CommandResult(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.data = null;
        this.exception = null;
    }

    public CommandResult(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.exception = null;
    }

    // Failure constructor
    public CommandResult(boolean success, String message, Exception exception) {
        this.success = success;
        this.message = message;
        this.data = null;
        this.exception = exception;
    }

    // Static factory methods
    public static CommandResult success(String message) {
        return new CommandResult(true, message);
    }

    public static CommandResult success(String message, Object data) {
        return new CommandResult(true, message, data);
    }

    public static CommandResult failure(String message) {
        return new CommandResult(false, message);
    }

    public static CommandResult failure(String message, Exception exception) {
        return new CommandResult(false, message, exception);
    }

    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Object getData() { return data; }
    public Exception getException() { return exception; }

    @SuppressWarnings("unchecked")
    public <T> T getData(Class<T> type) {
        if (data != null && type.isAssignableFrom(data.getClass())) {
            return (T) data;
        }
        return null;
    }
}
