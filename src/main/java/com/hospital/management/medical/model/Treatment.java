package com.hospital.management.medical.model;

import java.time.LocalDate;

public class Treatment {
    private int treatmentId;
    private int medicalRecordId;
    private String treatmentPlan;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer assignedByDoctorId;
    private String status;

    public Treatment() {}
    public Treatment(int medicalRecordId, String plan, LocalDate start, LocalDate end, Integer assignedByDoctorId) {
        this.medicalRecordId = medicalRecordId; this.treatmentPlan = plan; this.startDate = start;
        this.endDate = end; this.assignedByDoctorId = assignedByDoctorId;
    }
    // getters/setters

    public int getTreatmentId() {
        return treatmentId;
    }

    public void setTreatmentId(int treatmentId) {
        this.treatmentId = treatmentId;
    }

    public int getMedicalRecordId() {
        return medicalRecordId;
    }

    public void setMedicalRecordId(int medicalRecordId) {
        this.medicalRecordId = medicalRecordId;
    }

    public String getTreatmentPlan() {
        return treatmentPlan;
    }

    public void setTreatmentPlan(String treatmentPlan) {
        this.treatmentPlan = treatmentPlan;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getAssignedByDoctorId() {
        return assignedByDoctorId;
    }

    public void setAssignedByDoctorId(Integer assignedByDoctorId) {
        this.assignedByDoctorId = assignedByDoctorId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override public String toString() { return treatmentId + " | plan:" + treatmentPlan + " (" + status + ")"; }
}
