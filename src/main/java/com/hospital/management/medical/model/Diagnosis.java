package com.hospital.management.medical.model;

import java.time.LocalDateTime;

public class Diagnosis {
    private int diagnosisId;
    private int medicalRecordId;
    private String code;
    private String description;
    private String severity;
    private Integer diagnosedByDoctorId;
    private LocalDateTime diagnosedAt;

    public Diagnosis() {}
    public Diagnosis(int medicalRecordId, String code, String description, String severity, Integer diagnosedByDoctorId) {
        this.medicalRecordId = medicalRecordId; this.code = code; this.description = description;
        this.severity = severity; this.diagnosedByDoctorId = diagnosedByDoctorId;
    }
    // getters/setters

    public int getDiagnosisId() {
        return diagnosisId;
    }

    public void setDiagnosisId(int diagnosisId) {
        this.diagnosisId = diagnosisId;
    }

    public int getMedicalRecordId() {
        return medicalRecordId;
    }

    public void setMedicalRecordId(int medicalRecordId) {
        this.medicalRecordId = medicalRecordId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public Integer getDiagnosedByDoctorId() {
        return diagnosedByDoctorId;
    }

    public void setDiagnosedByDoctorId(Integer diagnosedByDoctorId) {
        this.diagnosedByDoctorId = diagnosedByDoctorId;
    }

    public LocalDateTime getDiagnosedAt() {
        return diagnosedAt;
    }

    public void setDiagnosedAt(LocalDateTime diagnosedAt) {
        this.diagnosedAt = diagnosedAt;
    }

    @Override public String toString() { return diagnosisId + " | " + code + " | " + description + " (" + severity + ")"; }
}
