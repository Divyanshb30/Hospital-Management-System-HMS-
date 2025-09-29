package com.hospital.management.billing.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * PaymentPlan
 * -----------
 * Represents an installment plan for paying off a bill.
 * A plan splits the total due into scheduled installments.
 */
public class PaymentPlan {

    private Long id;                        // Unique identifier
    private Long billId;                    // Linked bill
    private Long patientId;                 // Linked patient
    private BigDecimal totalAmount;         // Total bill amount to be paid via installments
    private int numberOfInstallments;       // Total number of installments
    private BigDecimal installmentAmount;   // Fixed amount per installment (if equal split)
    private String status;                  // ACTIVE, COMPLETED, DEFAULTED, CANCELLED

    private List<Installment> installments; // Individual installment schedule

    private LocalDateTime startDate;        // When the plan begins
    private LocalDateTime endDate;          // Expected completion date
    private LocalDateTime createdAt;        // Record creation timestamp
    private LocalDateTime updatedAt;        // Last updated timestamp

    // --- Nested Class for Installments ---
    public static class Installment {
        private int installmentNumber;          // e.g., 1, 2, 3
        private BigDecimal amount;              // Amount due
        private LocalDateTime dueDate;          // Due date for payment
        private boolean paid;                   // Paid or not
        private LocalDateTime paidAt;           // When it was paid

        public Installment() {}

        public Installment(int installmentNumber, BigDecimal amount,
                           LocalDateTime dueDate, boolean paid, LocalDateTime paidAt) {
            this.installmentNumber = installmentNumber;
            this.amount = amount;
            this.dueDate = dueDate;
            this.paid = paid;
            this.paidAt = paidAt;
        }

        public int getInstallmentNumber() { return installmentNumber; }
        public void setInstallmentNumber(int installmentNumber) { this.installmentNumber = installmentNumber; }

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }

        public LocalDateTime getDueDate() { return dueDate; }
        public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }

        public boolean isPaid() { return paid; }
        public void setPaid(boolean paid) { this.paid = paid; }

        public LocalDateTime getPaidAt() { return paidAt; }
        public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

        @Override
        public String toString() {
            return "Installment{" +
                    "installmentNumber=" + installmentNumber +
                    ", amount=" + amount +
                    ", dueDate=" + dueDate +
                    ", paid=" + paid +
                    ", paidAt=" + paidAt +
                    '}';
        }
    }

    // --- Constructors ---
    public PaymentPlan() {}

    public PaymentPlan(Long id, Long billId, Long patientId, BigDecimal totalAmount,
                       int numberOfInstallments, BigDecimal installmentAmount,
                       String status, List<Installment> installments,
                       LocalDateTime startDate, LocalDateTime endDate,
                       LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.billId = billId;
        this.patientId = patientId;
        this.totalAmount = totalAmount;
        this.numberOfInstallments = numberOfInstallments;
        this.installmentAmount = installmentAmount;
        this.status = status;
        this.installments = installments;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBillId() { return billId; }
    public void setBillId(Long billId) { this.billId = billId; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public int getNumberOfInstallments() { return numberOfInstallments; }
    public void setNumberOfInstallments(int numberOfInstallments) { this.numberOfInstallments = numberOfInstallments; }

    public BigDecimal getInstallmentAmount() { return installmentAmount; }
    public void setInstallmentAmount(BigDecimal installmentAmount) { this.installmentAmount = installmentAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<Installment> getInstallments() { return installments; }
    public void setInstallments(List<Installment> installments) { this.installments = installments; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // --- Utility Methods ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PaymentPlan)) return false;
        PaymentPlan that = (PaymentPlan) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "PaymentPlan{" +
                "id=" + id +
                ", billId=" + billId +
                ", patientId=" + patientId +
                ", totalAmount=" + totalAmount +
                ", numberOfInstallments=" + numberOfInstallments +
                ", installmentAmount=" + installmentAmount +
                ", status='" + status + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
