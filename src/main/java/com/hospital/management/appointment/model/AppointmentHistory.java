package com.hospital.management.appointment.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents the historical record of an appointment,
 * including status changes and notes for auditing.
 */
public class AppointmentHistory {

    private Long id;
    private Long appointmentId;     // reference to Appointment
    private Long patientId;
    private Long doctorId;
    private LocalDateTime timestamp; // when this record was logged
    private String status;           // e.g., SCHEDULED, RESCHEDULED, CANCELLED, COMPLETED
    private String remarks;          // optional notes or reason for status change

    public AppointmentHistory() {}

    public AppointmentHistory(Long id, Long appointmentId, Long patientId, Long doctorId,
                              LocalDateTime timestamp, String status, String remarks) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.timestamp = timestamp;
        this.status = status;
        this.remarks = remarks;
    }

    // --- Getters & Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    // --- equals & hashCode (based on id) ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppointmentHistory)) return false;
        AppointmentHistory that = (AppointmentHistory) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // --- toString for debug/CLI ---

    @Override
    public String toString() {
        return "AppointmentHistory{" +
                "id=" + id +
                ", appointmentId=" + appointmentId +
                ", patientId=" + patientId +
                ", doctorId=" + doctorId +
                ", timestamp=" + timestamp +
                ", status='" + status + '\'' +
                ", remarks='" + remarks + '\'' +
                '}';
    }
}
