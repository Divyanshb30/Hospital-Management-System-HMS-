package com.hospital.management.appointment.service;

import com.hospital.management.appointment.dao.AppointmentDAO;
import com.hospital.management.appointment.dao.AppointmentDAOImpl;
import com.hospital.management.appointment.model.Appointment;
import com.hospital.management.appointment.model.AppointmentConflict;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementation of AppointmentService.
 * Encapsulates business logic and uses DAO for persistence.
 */
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentDAO appointmentDAO;

    public AppointmentServiceImpl(AppointmentDAO appointmentDAO) {
        this.appointmentDAO = appointmentDAO;
    }

    /** Convenience constructor with default DAO impl. */
    public AppointmentServiceImpl() {
        this(new AppointmentDAOImpl());
    }

    @Override
    public Long schedule(Appointment appointment) {
        Objects.requireNonNull(appointment, "Appointment cannot be null");
        if (appointment.getDoctorId() == null || appointment.getPatientId() == null) {
            throw new IllegalArgumentException("Doctor and Patient IDs are required");
        }
        if (appointment.getAppointmentTime() == null) {
            throw new IllegalArgumentException("Appointment time is required");
        }

        // Conflict check before scheduling
        List<AppointmentConflict> conflicts =
                appointmentDAO.checkConflicts(appointment.getDoctorId(),
                        appointment.getAppointmentTime(),
                        appointment.getAppointmentTime().plusMinutes(30));

        if (!conflicts.isEmpty()) {
            throw new IllegalStateException("Appointment conflict detected: " + conflicts);
        }

        // Default status if not set
        if (appointment.getStatus() == null || appointment.getStatus().isBlank()) {
            appointment.setStatus("SCHEDULED");
        }

        return appointmentDAO.insert(appointment);
    }

    @Override
    public Optional<Appointment> getById(Long id) {
        return appointmentDAO.findById(id);
    }

    @Override
    public List<Appointment> listAll() {
        return appointmentDAO.findAll();
    }

    @Override
    public List<Appointment> listByPatient(Long patientId) {
        return appointmentDAO.findByPatientId(patientId);
    }

    @Override
    public List<Appointment> listByDoctor(Long doctorId) {
        return appointmentDAO.findByDoctorId(doctorId);
    }

    @Override
    public List<Appointment> listByDoctorAndRange(Long doctorId, LocalDateTime start, LocalDateTime end) {
        return appointmentDAO.findByDoctorAndTimeRange(doctorId, start, end);
    }

    @Override
    public List<Appointment> listByStatus(String status) {
        return appointmentDAO.findByStatus(status);
    }

    @Override
    public boolean reschedule(Long appointmentId, LocalDateTime newStartTime) {
        Optional<Appointment> existing = appointmentDAO.findById(appointmentId);
        if (existing.isEmpty()) return false;

        Appointment appt = existing.get();

        // Conflict check for new slot
        List<AppointmentConflict> conflicts =
                appointmentDAO.checkConflicts(appt.getDoctorId(),
                        newStartTime,
                        newStartTime.plusMinutes(30));

        if (!conflicts.isEmpty()) {
            throw new IllegalStateException("Reschedule conflict detected: " + conflicts);
        }

        appt.setAppointmentTime(newStartTime);
        appt.setStatus("RESCHEDULED");
        return appointmentDAO.update(appt);
    }

    @Override
    public boolean complete(Long appointmentId, String remarks) {
        Optional<Appointment> existing = appointmentDAO.findById(appointmentId);
        if (existing.isEmpty()) return false;

        Appointment appt = existing.get();
        appt.setStatus("COMPLETED");
        if (remarks != null) appt.setNotes(remarks);

        return appointmentDAO.update(appt);
    }

    @Override
    public boolean cancel(Long appointmentId, String reason) {
        Optional<Appointment> existing = appointmentDAO.findById(appointmentId);
        if (existing.isEmpty()) return false;

        Appointment appt = existing.get();
        appt.setStatus("CANCELLED");
        if (reason != null) appt.setNotes(reason);

        return appointmentDAO.update(appt);
    }

    @Override
    public boolean updateNotes(Long appointmentId, String notes) {
        Optional<Appointment> existing = appointmentDAO.findById(appointmentId);
        if (existing.isEmpty()) return false;

        Appointment appt = existing.get();
        appt.setNotes(notes);
        return appointmentDAO.update(appt);
    }

    @Override
    public boolean update(Appointment appointment) {
        Objects.requireNonNull(appointment, "Appointment cannot be null");
        if (appointment.getId() == null) {
            throw new IllegalArgumentException("Appointment ID is required for update");
        }
        return appointmentDAO.update(appointment);
    }

    @Override
    public boolean delete(Long id) {
        return appointmentDAO.deleteById(id);
    }

    @Override
    public List<AppointmentConflict> checkConflicts(Long doctorId, LocalDateTime start, LocalDateTime end) {
        return appointmentDAO.checkConflicts(doctorId, start, end);
    }

    @Override
    public long count() {
        return appointmentDAO.count();
    }
}
