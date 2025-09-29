package com.hospital.management.medical.model;

import java.time.LocalDate;

public class Prescription {
    private int prescriptionId;
    private int medicalRecordId;
    private String notes;
    private LocalDate dateIssued;

    public Prescription() {}
    public Prescription(int medicalRecordId, String notes, LocalDate dateIssued) {
        this.medicalRecordId = medicalRecordId; this.notes = notes; this.dateIssued = dateIssued;
    }
    // getters/setters

    public int getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(int prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public int getMedicalRecordId() {
        return medicalRecordId;
    }

    public void setMedicalRecordId(int medicalRecordId) {
        this.medicalRecordId = medicalRecordId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDate getDateIssued() {
        return dateIssued;
    }

    public void setDateIssued(LocalDate dateIssued) {
        this.dateIssued = dateIssued;
    }

    @Override public String toString() { return prescriptionId + " | record:" + medicalRecordId + " | " + notes; }
}
