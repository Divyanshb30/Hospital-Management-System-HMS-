package com.hospital.management.patient.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * PatientStats represents statistical/health tracking data
 * for a patient over time (vitals, admissions, visits, etc.).
 */
public class PatientStats {
    private Long id;              // Unique stats record (DB PK)
    private Long patientId;       // Link to Patient.id
    private LocalDate recordDate; // When the stats were recorded

    // --- Health metrics ---
    private Double height;        // cm
    private Double weight;        // kg
    private String bloodPressure; // e.g. "120/80"
    private Integer heartRate;    // bpm
    private Double temperature;   // Celsius
    private Integer oxygenSaturation; // %

    // --- Utilization stats ---
    private Integer totalVisits;   // Total visits to hospital
    private Integer totalAdmissions; // Number of times admitted
    private Integer totalPrescriptions; // Medicines prescribed count

    // --- Constructors ---
    public PatientStats() {}

    public PatientStats(Long id, Long patientId, LocalDate recordDate,
                        Double height, Double weight, String bloodPressure,
                        Integer heartRate, Double temperature, Integer oxygenSaturation,
                        Integer totalVisits, Integer totalAdmissions, Integer totalPrescriptions) {
        this.id = id;
        this.patientId = patientId;
        this.recordDate = recordDate;
        this.height = height;
        this.weight = weight;
        this.bloodPressure = bloodPressure;
        this.heartRate = heartRate;
        this.temperature = temperature;
        this.oxygenSaturation = oxygenSaturation;
        this.totalVisits = totalVisits;
        this.totalAdmissions = totalAdmissions;
        this.totalPrescriptions = totalPrescriptions;
    }

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public LocalDate getRecordDate() { return recordDate; }
    public void setRecordDate(LocalDate recordDate) { this.recordDate = recordDate; }

    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public String getBloodPressure() { return bloodPressure; }
    public void setBloodPressure(String bloodPressure) { this.bloodPressure = bloodPressure; }

    public Integer getHeartRate() { return heartRate; }
    public void setHeartRate(Integer heartRate) { this.heartRate = heartRate; }

    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }

    public Integer getOxygenSaturation() { return oxygenSaturation; }
    public void setOxygenSaturation(Integer oxygenSaturation) { this.oxygenSaturation = oxygenSaturation; }

    public Integer getTotalVisits() { return totalVisits; }
    public void setTotalVisits(Integer totalVisits) { this.totalVisits = totalVisits; }

    public Integer getTotalAdmissions() { return totalAdmissions; }
    public void setTotalAdmissions(Integer totalAdmissions) { this.totalAdmissions = totalAdmissions; }

    public Integer getTotalPrescriptions() { return totalPrescriptions; }
    public void setTotalPrescriptions(Integer totalPrescriptions) { this.totalPrescriptions = totalPrescriptions; }

    // --- Equality & Hashing ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PatientStats)) return false;
        PatientStats that = (PatientStats) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return (id != null) ? id.hashCode() : super.hashCode();
    }

    @Override
    public String toString() {
        return "PatientStats{" +
                "id=" + id +
                ", patientId=" + patientId +
                ", recordDate=" + recordDate +
                ", height=" + height +
                ", weight=" + weight +
                ", bloodPressure='" + bloodPressure + '\'' +
                ", heartRate=" + heartRate +
                ", temperature=" + temperature +
                ", oxygenSaturation=" + oxygenSaturation +
                ", totalVisits=" + totalVisits +
                ", totalAdmissions=" + totalAdmissions +
                ", totalPrescriptions=" + totalPrescriptions +
                '}';
    }
}
