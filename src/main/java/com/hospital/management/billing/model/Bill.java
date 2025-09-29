package com.hospital.management.billing.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Bill
 * ----
 * Represents a patient bill in the hospital system.
 * Stores billing amount, status, and audit details.
 */
public class Bill {

    private Long id;                     // Unique identifier for the bill
    private Long patientId;              // Linked patient (foreign key)
    private Long appointmentId;          // Linked appointment (if applicable)
    private BigDecimal totalAmount;      // Total billable amount
    private BigDecimal amountPaid;       // Amount already paid
    private String status;               // PENDING, PAID, PARTIAL, CANCELLED
    private LocalDateTime billingDate;   // When bill was generated
    private LocalDateTime dueDate;       // Payment due date
    private LocalDateTime createdAt;     // Record creation timestamp
    private LocalDateTime updatedAt;     // Record last updated

    // --- Constructors ---
    public Bill() {}

    public Bill(Long id, Long patientId, Long appointmentId,
                BigDecimal totalAmount, BigDecimal amountPaid, String status,
                LocalDateTime billingDate, LocalDateTime dueDate,
                LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.patientId = patientId;
        this.appointmentId = appointmentId;
        this.totalAmount = totalAmount;
        this.amountPaid = amountPaid;
        this.status = status;
        this.billingDate = billingDate;
        this.dueDate = dueDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public Long getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getBillingDate() { return billingDate; }
    public void setBillingDate(LocalDateTime billingDate) { this.billingDate = billingDate; }

    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // --- Utility Methods ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bill)) return false;
        Bill bill = (Bill) o;
        return Objects.equals(id, bill.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Bill{" +
                "id=" + id +
                ", patientId=" + patientId +
                ", appointmentId=" + appointmentId +
                ", totalAmount=" + totalAmount +
                ", amountPaid=" + amountPaid +
                ", status='" + status + '\'' +
                ", billingDate=" + billingDate +
                ", dueDate=" + dueDate +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
