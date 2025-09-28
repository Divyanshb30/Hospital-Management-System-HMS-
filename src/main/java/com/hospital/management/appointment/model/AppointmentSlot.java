package com.hospital.management.appointment.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a specific time slot for doctor availability and appointments.
 */
public class AppointmentSlot {

    private Long id;
    private Long doctorId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean available;   // true = free, false = booked

    public AppointmentSlot() {}

    public AppointmentSlot(Long id, Long doctorId, LocalDateTime startTime,
                           LocalDateTime endTime, boolean available) {
        this.id = id;
        this.doctorId = doctorId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.available = available;
    }

    // --- Getters & Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    // --- Utility Methods ---

    /** Duration of the slot in minutes. */
    public long getDurationMinutes() {
        return java.time.Duration.between(startTime, endTime).toMinutes();
    }

    // --- equals & hashCode (based on id) ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppointmentSlot)) return false;
        AppointmentSlot slot = (AppointmentSlot) o;
        return Objects.equals(id, slot.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // --- toString for CLI/debug output ---
    @Override
    public String toString() {
        return "AppointmentSlot{" +
                "id=" + id +
                ", doctorId=" + doctorId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", available=" + available +
                '}';
    }
}
