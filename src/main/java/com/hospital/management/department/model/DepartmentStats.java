package com.hospital.management.department.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * DepartmentStats
 * ----------------
 * Represents statistical and performance metrics of a hospital department.
 * Useful for reporting, analytics, and dashboards.
 */
public class DepartmentStats {

    private Long id;                  // Unique identifier for stats record
    private Long departmentId;        // Link to Department (foreign key)

    private int totalPatients;        // Patients treated in this department
    private int activePatients;       // Currently admitted/active patients
    private int dischargedPatients;   // Patients discharged
    private int staffCount;           // Doctors + nurses + support staff
    private double avgWaitTime;       // Average patient wait time (minutes)
    private double occupancyRate;     // Beds occupied vs. total (%)

    private LocalDate statsDate;      // Date of snapshot

    // --- Constructors ---
    public DepartmentStats() {}

    public DepartmentStats(Long id, Long departmentId, int totalPatients,
                           int activePatients, int dischargedPatients, int staffCount,
                           double avgWaitTime, double occupancyRate, LocalDate statsDate) {
        this.id = id;
        this.departmentId = departmentId;
        this.totalPatients = totalPatients;
        this.activePatients = activePatients;
        this.dischargedPatients = dischargedPatients;
        this.staffCount = staffCount;
        this.avgWaitTime = avgWaitTime;
        this.occupancyRate = occupancyRate;
        this.statsDate = statsDate;
    }

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public int getTotalPatients() { return totalPatients; }
    public void setTotalPatients(int totalPatients) { this.totalPatients = totalPatients; }

    public int getActivePatients() { return activePatients; }
    public void setActivePatients(int activePatients) { this.activePatients = activePatients; }

    public int getDischargedPatients() { return dischargedPatients; }
    public void setDischargedPatients(int dischargedPatients) { this.dischargedPatients = dischargedPatients; }

    public int getStaffCount() { return staffCount; }
    public void setStaffCount(int staffCount) { this.staffCount = staffCount; }

    public double getAvgWaitTime() { return avgWaitTime; }
    public void setAvgWaitTime(double avgWaitTime) { this.avgWaitTime = avgWaitTime; }

    public double getOccupancyRate() { return occupancyRate; }
    public void setOccupancyRate(double occupancyRate) { this.occupancyRate = occupancyRate; }

    public LocalDate getStatsDate() { return statsDate; }
    public void setStatsDate(LocalDate statsDate) { this.statsDate = statsDate; }

    // --- Utility methods ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DepartmentStats)) return false;
        DepartmentStats that = (DepartmentStats) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "DepartmentStats{" +
                "id=" + id +
                ", departmentId=" + departmentId +
                ", totalPatients=" + totalPatients +
                ", activePatients=" + activePatients +
                ", dischargedPatients=" + dischargedPatients +
                ", staffCount=" + staffCount +
                ", avgWaitTime=" + avgWaitTime +
                ", occupancyRate=" + occupancyRate +
                ", statsDate=" + statsDate +
                '}';
    }
}
