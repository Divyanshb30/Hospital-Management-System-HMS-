package com.hospital.management.billing.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Invoice
 * -------
 * Represents a detailed invoice generated for a patient bill.
 * Aggregates billing, payment, and hospital details.
 */
public class Invoice {

    private Long id;                        // Unique identifier for the invoice
    private Long billId;                    // Linked Bill
    private Long patientId;                 // Linked Patient
    private String invoiceNumber;           // Human-readable invoice number
    private BigDecimal totalAmount;         // Total amount billed
    private BigDecimal amountPaid;          // Total amount paid
    private BigDecimal balanceDue;          // Remaining amount (total - paid)
    private String status;                  // GENERATED, SENT, PAID, CANCELLED

    private List<Payment> payments;         // Payment records linked to this invoice

    private LocalDateTime issuedDate;       // Invoice creation date
    private LocalDateTime dueDate;          // Payment due date
    private LocalDateTime createdAt;        // DB audit field
    private LocalDateTime updatedAt;        // DB audit field

    // --- Constructors ---
    public Invoice() {}

    public Invoice(Long id, Long billId, Long patientId, String invoiceNumber,
                   BigDecimal totalAmount, BigDecimal amountPaid, BigDecimal balanceDue,
                   String status, List<Payment> payments,
                   LocalDateTime issuedDate, LocalDateTime dueDate,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.billId = billId;
        this.patientId = patientId;
        this.invoiceNumber = invoiceNumber;
        this.totalAmount = totalAmount;
        this.amountPaid = amountPaid;
        this.balanceDue = balanceDue;
        this.status = status;
        this.payments = payments;
        this.issuedDate = issuedDate;
        this.dueDate = dueDate;
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

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }

    public BigDecimal getBalanceDue() { return balanceDue; }
    public void setBalanceDue(BigDecimal balanceDue) { this.balanceDue = balanceDue; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<Payment> getPayments() { return payments; }
    public void setPayments(List<Payment> payments) { this.payments = payments; }

    public LocalDateTime getIssuedDate() { return issuedDate; }
    public void setIssuedDate(LocalDateTime issuedDate) { this.issuedDate = issuedDate; }

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
        if (!(o instanceof Invoice)) return false;
        Invoice invoice = (Invoice) o;
        return Objects.equals(id, invoice.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Invoice{" +
                "id=" + id +
                ", billId=" + billId +
                ", patientId=" + patientId +
                ", invoiceNumber='" + invoiceNumber + '\'' +
                ", totalAmount=" + totalAmount +
                ", amountPaid=" + amountPaid +
                ", balanceDue=" + balanceDue +
                ", status='" + status + '\'' +
                ", issuedDate=" + issuedDate +
                ", dueDate=" + dueDate +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
