package com.hospital.management.common.constants;

/**
 * Database-related constants used across the application
 */
public final class DatabaseConstants {

    // Table Names
    public static final String TABLE_PATIENTS = "patients";
    public static final String TABLE_DOCTORS = "doctors";
    public static final String TABLE_APPOINTMENTS = "appointments";
    public static final String TABLE_SPECIALIZATIONS = "specializations";
    public static final String TABLE_DOCTOR_SCHEDULES = "doctor_schedules";

    // Common Column Names
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_UPDATED_AT = "updated_at";
    public static final String COLUMN_STATUS = "status";

    // SQL Queries - Common Patterns
    public static final String SELECT_BY_ID = "SELECT * FROM %s WHERE id = ?";
    public static final String SELECT_ALL = "SELECT * FROM %s";
    public static final String DELETE_BY_ID = "DELETE FROM %s WHERE id = ?";
    public static final String COUNT_ALL = "SELECT COUNT(*) FROM %s";

    private DatabaseConstants() {
        // Prevent instantiation
    }
}
