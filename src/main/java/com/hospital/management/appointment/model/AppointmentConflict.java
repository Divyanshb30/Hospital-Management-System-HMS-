package com.hospital.management.appointment.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a scheduling conflict between appointments.
 * Used to detect overlapping appointment slots for a doctor or patient.
 */
public class AppointmentConflict {

    private Long id;
    private Long appointmentId1;       // first conflicting appointment
    private Long appointmentId2;       // second conflicting appointment
    private Long doctorId;
    private Long patientId;            // optional: for patient double-booking
    private LocalDateTime conflictStart;
    private LocalDateTime conflictEnd;
    private String reason;             // e.g., "Overlapping slots", "Double booking"

    public AppointmentConflict() {}

    public AppointmentConflict(Long id, Long appointmentId1, Long appointmentId2,
                               Long doctorId, Long patientId,
                               LocalDateTime conflictStart, LocalDateTime conflictEnd,
                               String reason) {
        this.id = id;
        this.appointmentId1 = appointmentId1;
        this.appointmentId2 = appointmentId2;
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.conflictStart = conflictStart;
        this.conflictEnd = conflictEnd;
        this.reason = reason;
    }

    // --- Getters & Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAppointmentId1() {
        return appointmentId1;
    }

    public void setAppointmentId1(Long appointmentId1) {
        this.appointmentId1 = appointmentId1;
    }

    public Long getAppointmentId2() {
        return appointmentId2;
    }

    public void setAppointmentId2(Long appointmentId2) {
        this.appointmentId2 = appointmentId2;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public LocalDateTime getConflictStart() {
        return conflictStart;
    }

    public void setConflictStart(LocalDateTime conflictStart) {
        this.conflictStart = conflictStart;
    }

    public LocalDateTime getConflictEnd() {
        return conflictEnd;
    }

    public void setConflictEnd(LocalDateTime conflictEnd) {
        this.conflictEnd = conflictEnd;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    // --- equals & hashCode (based on id) ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppointmentConflict)) return false;
        AppointmentConflict that = (AppointmentConflict) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // --- toString for CLI/debug ---
    @Override
    public String toString() {
        return "AppointmentConflict{" +
                "id=" + id +
                ", appointmentId1=" + appointmentId1 +
                ", appointmentId2=" + appointmentId2 +
                ", doctorId=" + doctorId +
                ", patientId=" + patientId +
                ", conflictStart=" + conflictStart +
                ", conflictEnd=" + conflictEnd +
                ", reason='" + reason + '\'' +
                '}';
    }

    // --- Utility method for detection ---
    public static boolean isOverlapping(LocalDateTime start1, LocalDateTime end1,
                                        LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }
}
