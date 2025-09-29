package com.hospital.management.doctor.model;

public class DoctorStats {
    private Integer doctorId;
    private Integer totalAppointments;
    private Integer completedAppointments;
    private Integer cancelledAppointments;
    private Double averageRating;

    // Constructors
    public DoctorStats() {}

    public DoctorStats(Integer doctorId) {
        this.doctorId = doctorId;
        this.totalAppointments = 0;
        this.completedAppointments = 0;
        this.cancelledAppointments = 0;
        this.averageRating = 0.0;
    }

    // Getters and Setters
    public Integer getDoctorId() { return doctorId; }
    public void setDoctorId(Integer doctorId) { this.doctorId = doctorId; }

    public Integer getTotalAppointments() { return totalAppointments; }
    public void setTotalAppointments(Integer totalAppointments) { this.totalAppointments = totalAppointments; }

    public Integer getCompletedAppointments() { return completedAppointments; }
    public void setCompletedAppointments(Integer completedAppointments) { this.completedAppointments = completedAppointments; }

    public Integer getCancelledAppointments() { return cancelledAppointments; }
    public void setCancelledAppointments(Integer cancelledAppointments) { this.cancelledAppointments = cancelledAppointments; }

    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

    // Computed properties
    public Integer getPendingAppointments() {
        return totalAppointments - completedAppointments - cancelledAppointments;
    }

    public Double getCompletionRate() {
        return totalAppointments > 0 ? (completedAppointments.doubleValue() / totalAppointments) * 100 : 0.0;
    }

    @Override
    public String toString() {
        return String.format("DoctorStats{doctorId=%d, total=%d, completed=%d, cancelled=%d, pending=%d, rating=%.2f, completion=%.1f%%}",
                doctorId, totalAppointments, completedAppointments, cancelledAppointments,
                getPendingAppointments(), averageRating, getCompletionRate());
    }
}
