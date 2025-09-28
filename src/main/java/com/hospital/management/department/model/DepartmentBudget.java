package com.hospital.management.department.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * DepartmentBudget
 * -----------------
 * Represents the financial allocation and tracking of a hospital department.
 * Captures planned vs actual spending, revenue, and variance.
 */
public class DepartmentBudget {

    private Long id;                   // Unique identifier for budget record
    private Long departmentId;         // Link to Department (foreign key)

    private BigDecimal allocatedBudget; // Planned/approved budget
    private BigDecimal actualExpenditure; // Real expenditure so far
    private BigDecimal revenueGenerated;  // Income (billing, insurance, etc.)
    private BigDecimal variance;          // allocatedBudget - actualExpenditure

    private LocalDate startDate;       // Budget period start
    private LocalDate endDate;         // Budget period end

    // --- Constructors ---
    public DepartmentBudget() {}

    public DepartmentBudget(Long id, Long departmentId,
                            BigDecimal allocatedBudget, BigDecimal actualExpenditure,
                            BigDecimal revenueGenerated, BigDecimal variance,
                            LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.departmentId = departmentId;
        this.allocatedBudget = allocatedBudget;
        this.actualExpenditure = actualExpenditure;
        this.revenueGenerated = revenueGenerated;
        this.variance = variance;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public BigDecimal getAllocatedBudget() { return allocatedBudget; }
    public void setAllocatedBudget(BigDecimal allocatedBudget) { this.allocatedBudget = allocatedBudget; }

    public BigDecimal getActualExpenditure() { return actualExpenditure; }
    public void setActualExpenditure(BigDecimal actualExpenditure) { this.actualExpenditure = actualExpenditure; }

    public BigDecimal getRevenueGenerated() { return revenueGenerated; }
    public void setRevenueGenerated(BigDecimal revenueGenerated) { this.revenueGenerated = revenueGenerated; }

    public BigDecimal getVariance() { return variance; }
    public void setVariance(BigDecimal variance) { this.variance = variance; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    // --- Utility methods ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DepartmentBudget)) return false;
        DepartmentBudget that = (DepartmentBudget) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "DepartmentBudget{" +
                "id=" + id +
                ", departmentId=" + departmentId +
                ", allocatedBudget=" + allocatedBudget +
                ", actualExpenditure=" + actualExpenditure +
                ", revenueGenerated=" + revenueGenerated +
                ", variance=" + variance +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
