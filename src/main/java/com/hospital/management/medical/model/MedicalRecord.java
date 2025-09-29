package com.hospital.management.medical.model;

import java.time.LocalDateTime;

public class MedicalRecord {
    private int medicalRecordId;
    private int patientId;
    private Integer createdByDoctorId;
    private String chiefComplaint;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MedicalRecord() {}
    public MedicalRecord(int patientId, Integer createdByDoctorId, String chiefComplaint, String notes) {
        this.patientId = patientId;
        this.createdByDoctorId = createdByDoctorId;
        this.chiefComplaint = chiefComplaint;
        this.notes = notes;
    }
    // getters/setters

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getMedicalRecordId() {
        return medicalRecordId;
    }

    public void setMedicalRecordId(int medicalRecordId) {
        this.medicalRecordId = medicalRecordId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public Integer getCreatedByDoctorId() {
        return createdByDoctorId;
    }

    public void setCreatedByDoctorId(Integer createdByDoctorId) {
        this.createdByDoctorId = createdByDoctorId;
    }

    public String getChiefComplaint() {
        return chiefComplaint;
    }

    public void setChiefComplaint(String chiefComplaint) {
        this.chiefComplaint = chiefComplaint;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // toString
    @Override
    public String toString() {
        return medicalRecordId + " | patient:" + patientId + " | doctor:" + createdByDoctorId + " | " + chiefComplaint;
    }
    // getters/setters omitted for brevity
}
