package com.hospital.management.appointment.service;

import com.hospital.management.appointment.model.Appointment;
import com.hospital.management.appointment.model.AppointmentConflict;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for Appointment operations.
 * Encapsulates business rules (e.g., conflict checks, status transitions)
 * and delegates persistence to the DAO layer.
 */
public interface AppointmentService {

    /** Create/schedule a new appointment (performs conflict checks). Returns generated ID. */
    Long schedule(Appointment appointment);

    /** Fetch a single appointment by ID. */
    Optional<Appointment> getById(Long id);

    /** List all appointments (consider pagination in impl). */
    List<Appointment> listAll();

    /** List appointments for a specific patient. */
    List<Appointment> listByPatient(Long patientId);

    /** List appointments for a specific doctor. */
    List<Appointment> listByDoctor(Long doctorId);

    /** List a doctor's appointments within a time range. */
    List<Appointment> listByDoctorAndRange(Long doctorId, LocalDateTime start, LocalDateTime end);

    /** List appointments by status (e.g., SCHEDULED, COMPLETED, CANCELLED). */
    List<Appointment> listByStatus(String status);

    /** Reschedule an appointment; typically checks conflicts and updates time. */
    boolean reschedule(Long appointmentId, LocalDateTime newStartTime);

    /** Mark appointment as COMPLETED (also good place to append history). */
    boolean complete(Long appointmentId, String remarks);

    /** Cancel appointment with a reason (status = CANCELLED). */
    boolean cancel(Long appointmentId, String reason);

    /** Update free-form notes (without changing status/time). */
    boolean updateNotes(Long appointmentId, String notes);

    /** Generic update (patientId/doctorId/time/status/notes) when needed. */
    boolean update(Appointment appointment);

    /** Delete an appointment by ID. */
    boolean delete(Long id);

    /** Check potential conflicts for a doctor/time window before scheduling/rescheduling. */
    List<AppointmentConflict> checkConflicts(Long doctorId, LocalDateTime start, LocalDateTime end);

    /** Total number of appointments (for dashboards/pagination). */
    long count();
}
