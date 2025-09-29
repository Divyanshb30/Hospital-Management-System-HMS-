package com.hospital.management.models;

import com.hospital.management.common.enums.AppointmentStatus;
import com.hospital.management.common.exceptions.ValidationException;
import com.hospital.management.common.utils.DateTimeUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Appointment model representing patient-doctor appointments
 */
public class Appointment {

    private Long id;
    private Long patientId;
    private Long doctorId;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private AppointmentStatus status;
    private String reason;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor
    public Appointment() {
        this.status = AppointmentStatus.SCHEDULED;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor with essential fields
    public Appointment(Long patientId, Long doctorId, LocalDate appointmentDate, LocalTime appointmentTime) {
        this();
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
    }

    // Constructor with reason
    public Appointment(Long patientId, Long doctorId, LocalDate appointmentDate, LocalTime appointmentTime, String reason) {
        this(patientId, doctorId, appointmentDate, appointmentTime);
        this.reason = reason;
    }

    // Full constructor
    public Appointment(Long id, Long patientId, Long doctorId, LocalDate appointmentDate,
                       LocalTime appointmentTime, AppointmentStatus status, String reason,
                       String notes, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.reason = reason;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Validates the appointment object
     */
    public void validate() throws ValidationException {
        if (patientId == null) {
            throw new ValidationException("Patient ID is required", "PatientId");
        }

        if (doctorId == null) {
            throw new ValidationException("Doctor ID is required", "DoctorId");
        }

        if (appointmentDate == null) {
            throw new ValidationException("Appointment date is required", "AppointmentDate");
        }

        if (appointmentTime == null) {
            throw new ValidationException("Appointment time is required", "AppointmentTime");
        }

        if (status == null) {
            throw new ValidationException("Appointment status is required", "Status");
        }

        // Validate appointment is not in the past
        LocalDateTime appointmentDateTime = LocalDateTime.of(appointmentDate, appointmentTime);
        if (DateTimeUtil.isPast(appointmentDateTime)) {
            throw new ValidationException("Appointment cannot be scheduled in the past", "DateTime", appointmentDateTime);
        }

        // Validate appointment is within business hours
        if (!DateTimeUtil.isBusinessHours(appointmentTime)) {
            throw new ValidationException("Appointment must be within business hours (9 AM - 5 PM)", "AppointmentTime", appointmentTime);
        }

        // Validate appointment is on a weekday
        if (!DateTimeUtil.isWeekday(appointmentDate)) {
            throw new ValidationException("Appointments can only be scheduled on weekdays", "AppointmentDate", appointmentDate);
        }

        // Validate reason length if provided
        if (reason != null && reason.length() > 255) {
            throw new ValidationException("Appointment reason cannot exceed 255 characters", "Reason", reason);
        }
    }

    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    // Business methods
    public LocalDateTime getAppointmentDateTime() {
        if (appointmentDate == null || appointmentTime == null) return null;
        return LocalDateTime.of(appointmentDate, appointmentTime);
    }

    public boolean isToday() {
        return appointmentDate != null && DateTimeUtil.isToday(appointmentDate);
    }

    public boolean isFuture() {
        LocalDateTime dateTime = getAppointmentDateTime();
        return dateTime != null && DateTimeUtil.isFuture(dateTime);
    }

    public boolean isPast() {
        LocalDateTime dateTime = getAppointmentDateTime();
        return dateTime != null && DateTimeUtil.isPast(dateTime);
    }

    public boolean isActive() {
        return status != null && status.isActive();
    }

    public boolean isCompleted() {
        return status != null && status.isCompleted();
    }

    public boolean canBeModified() {
        return status == AppointmentStatus.SCHEDULED && isFuture();
    }

    public boolean canBeCancelled() {
        return (status == AppointmentStatus.SCHEDULED || status == AppointmentStatus.RESCHEDULED) && isFuture();
    }

    public void markAsCompleted() {
        this.status = AppointmentStatus.COMPLETED;
        updateTimestamp();
    }

    public void markAsCancelled() {
        this.status = AppointmentStatus.CANCELLED;
        updateTimestamp();
    }

    public void markAsInProgress() {
        this.status = AppointmentStatus.IN_PROGRESS;
        updateTimestamp();
    }

    public void markAsNoShow() {
        this.status = AppointmentStatus.NO_SHOW;
        updateTimestamp();
    }

    public String getDisplayDateTime() {
        return DateTimeUtil.formatForDisplay(getAppointmentDateTime());
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) {
        this.patientId = patientId;
        updateTimestamp();
    }

    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
        updateTimestamp();
    }

    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
        updateTimestamp();
    }

    public LocalTime getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
        updateTimestamp();
    }

    public AppointmentStatus getStatus() { return status; }
    public void setStatus(AppointmentStatus status) {
        this.status = status;
        updateTimestamp();
    }

    public String getReason() { return reason; }
    public void setReason(String reason) {
        this.reason = reason;
        updateTimestamp();
    }

    public String getNotes() { return notes; }
    public void setNotes(String notes) {
        this.notes = notes;
        updateTimestamp();
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Appointment)) return false;
        Appointment that = (Appointment) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(patientId, that.patientId) &&
                Objects.equals(doctorId, that.doctorId) &&
                Objects.equals(appointmentDate, that.appointmentDate) &&
                Objects.equals(appointmentTime, that.appointmentTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, patientId, doctorId, appointmentDate, appointmentTime);
    }

    @Override
    public String toString() {
        return String.format("Appointment{id=%d, patient=%d, doctor=%d, date='%s', time='%s', status=%s}",
                id, patientId, doctorId, appointmentDate, appointmentTime, status);
    }
}
