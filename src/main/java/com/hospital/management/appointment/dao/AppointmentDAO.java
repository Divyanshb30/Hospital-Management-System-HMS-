package com.hospital.management.appointment.dao;

import com.hospital.management.appointment.model.Appointment;
import com.hospital.management.appointment.model.AppointmentConflict;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object (DAO) interface for Appointment entity.
 * Defines CRUD operations and conflict-detection queries.
 */
public interface AppointmentDAO {

    /** Insert a new appointment and return generated ID. */
    Long insert(Appointment appointment);

    /** Find appointment by ID. */
    Optional<Appointment> findById(Long id);

    /** Get all appointments (later can be paginated). */
    List<Appointment> findAll();

    /** Find all appointments for a given patient. */
    List<Appointment> findByPatientId(Long patientId);

    /** Find all appointments for a given doctor. */
    List<Appointment> findByDoctorId(Long doctorId);

    /** Find all appointments for a doctor within a date range. */
    List<Appointment> findByDoctorAndTimeRange(Long doctorId, LocalDateTime start, LocalDateTime end);

    /** Search appointments by status. */
    List<Appointment> findByStatus(String status);

    /** Update appointment details. */
    boolean update(Appointment appointment);

    /** Delete an appointment by ID. */
    boolean deleteById(Long id);

    /** Count total appointments. */
    long count();

    // --- Conflict-specific operations ---

    /**
     * Check if a new appointment conflicts with existing ones for a doctor.
     * Returns a list of conflicts (could be empty if no clash).
     */
    List<AppointmentConflict> checkConflicts(Long doctorId, LocalDateTime start, LocalDateTime end);
}
