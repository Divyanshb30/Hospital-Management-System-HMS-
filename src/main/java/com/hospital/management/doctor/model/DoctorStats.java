package com.hospital.management.doctor.model;

public class DoctorStats {
    private int doctorId;
    private int totalAppointments;
    private int completedAppointments;
    private double averageRating;

    // getters/setters

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public int getTotalAppointments() {
        return totalAppointments;
    }

    public void setTotalAppointments(int totalAppointments) {
        this.totalAppointments = totalAppointments;
    }

    public int getCompletedAppointments() {
        return completedAppointments;
    }

    public void setCompletedAppointments(int completedAppointments) {
        this.completedAppointments = completedAppointments;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    @Override
    public String toString() {
        return String.format("Doctor %d -> total:%d completed:%d avgRating:%.2f",
                doctorId, totalAppointments, completedAppointments, averageRating);
    }
}
