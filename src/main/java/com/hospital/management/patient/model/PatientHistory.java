package com.hospital.management.patient.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * PatientHistory represents a medical record entry for a patient.
 */
public class PatientHistory {
    private Long id;              // Unique history record
    private Long patientId;       // Links to Patient.id
    private Long doctorId;        // Links to Doctor.id
    private LocalDate recordDate; // Date of entry

    private String diagnosis;     // Illness/condition
    private String treatment;     // Treatment provided
    private String prescriptions; // Medicines prescribed
    private String testsOrdered;  // Lab tests
    private LocalDate followUpDate;
    private String notes;         // Extra remarks

    // --- Constructors ---
    public PatientHistory() {}

    public PatientHistory(Long id, Long patientId, Long doctorId,
                          LocalDate recordDate, String diagnosis,
                          String treatment, String prescriptions,
                          String testsOrdered, LocalDate followUpDate,
                          String notes) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.recordDate = recordDate;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.prescriptions = prescriptions;
        this.testsOrdered = testsOrdered;
        this.followUpDate = followUpDate;
        this.notes = notes;
    }

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }

    public LocalDate getRecordDate() { return recordDate; }
    public void setRecordDate(LocalDate recordDate) { this.recordDate = recordDate; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public String getTreatment() { return treatment; }
    public void setTreatment(String treatment) { this.treatment = treatment; }

    public String getPrescriptions() { return prescriptions; }
    public void setPrescriptions(String prescriptions) { this.prescriptions = prescriptions; }

    public String getTestsOrdered() { return testsOrdered; }
    public void setTestsOrdered(String testsOrdered) { this.testsOrdered = testsOrdered; }

    public LocalDate getFollowUpDate() { return followUpDate; }
    public void setFollowUpDate(LocalDate followUpDate) { this.followUpDate = followUpDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    // --- Equality & Hashing ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PatientHistory)) return false;
        PatientHistory that = (PatientHistory) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return (id != null) ? id.hashCode() : super.hashCode();
    }

    @Override
    public String toString() {
        return "PatientHistory{" +
                "id=" + id +
                ", patientId=" + patientId +
                ", doctorId=" + doctorId +
                ", recordDate=" + recordDate +
                ", diagnosis='" + diagnosis + '\'' +
                ", treatment='" + treatment + '\'' +
                ", prescriptions='" + prescriptions + '\'' +
                ", testsOrdered='" + testsOrdered + '\'' +
                ", followUpDate=" + followUpDate +
                ", notes='" + notes + '\'' +
                '}';
    }
}