package com.hospital.management.billing.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Payment
 * -------
 * Represents a payment made towards a patient's bill.
 * Supports partial payments, multiple methods, and audit tracking.
 */
public class Payment {

    private Long id;                    // Unique identifier for the payment
    private Long billId;                // Foreign key to Bill
    private BigDecimal amount;          // Payment amount
    private String method;              // CASH, CARD, UPI, INSURANCE, etc.
    private String status;              // PENDING, COMPLETED, FAILED, REFUNDED
    private String transactionRef;      // Reference/transaction ID from payment gateway/bank
    private LocalDateTime paymentDate;  // When payment was made
    private LocalDateTime createdAt;    // Record creation timestamp
    private LocalDateTime updatedAt;    // Last updated timestamp

    // --- Constructors ---
    public Payment() {}

    public Payment(Long id, Long billId, BigDecimal amount, String method, String status,
                   String transactionRef, LocalDateTime paymentDate,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.billId = billId;
        this.amount = amount;
        this.method = method;
        this.status = status;
        this.transactionRef = transactionRef;
        this.paymentDate = paymentDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBillId() { return billId; }
    public void setBillId(Long billId) { this.billId = billId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTransactionRef() { return transactionRef; }
    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }

    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // --- Utility Methods ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Payment)) return false;
        Payment payment = (Payment) o;
        return Objects.equals(id, payment.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", billId=" + billId +
                ", amount=" + amount +
                ", method='" + method + '\'' +
                ", status='" + status + '\'' +
                ", transactionRef='" + transactionRef + '\'' +
                ", paymentDate=" + paymentDate +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
