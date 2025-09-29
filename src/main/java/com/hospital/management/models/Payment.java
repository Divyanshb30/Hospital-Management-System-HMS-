package com.hospital.management.models;

import com.hospital.management.common.enums.PaymentMethod;
import com.hospital.management.common.enums.PaymentStatus;
import com.hospital.management.common.exceptions.ValidationException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Payment model representing payment transactions for bills
 */
public class Payment {

    private Long id;
    private Long billId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private String transactionId;
    private LocalDateTime paymentDate;
    private PaymentStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor
    public Payment() {
        this.status = PaymentStatus.PENDING;
        this.paymentDate = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor with essential fields
    public Payment(Long billId, BigDecimal amount, PaymentMethod paymentMethod) {
        this();
        this.billId = billId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.transactionId = generateTransactionId();
    }

    // Constructor with transaction ID
    public Payment(Long billId, BigDecimal amount, PaymentMethod paymentMethod, String transactionId) {
        this();
        this.billId = billId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId != null ? transactionId : generateTransactionId();
    }

    // Full constructor
    public Payment(Long id, Long billId, BigDecimal amount, PaymentMethod paymentMethod,
                   String transactionId, LocalDateTime paymentDate, PaymentStatus status,
                   String notes, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.billId = billId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.paymentDate = paymentDate;
        this.status = status;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Validates the payment object
     */
    public void validate() throws ValidationException {
        if (billId == null) {
            throw new ValidationException("Bill ID is required", "BillId");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Payment amount must be positive", "Amount", amount);
        }

        if (paymentMethod == null) {
            throw new ValidationException("Payment method is required", "PaymentMethod");
        }

        if (status == null) {
            throw new ValidationException("Payment status is required", "Status");
        }

        if (paymentDate == null) {
            throw new ValidationException("Payment date is required", "PaymentDate");
        }

        // Validate transaction ID for digital payments
        if (paymentMethod != null && paymentMethod.isDigital()) {
            if (transactionId == null || transactionId.trim().isEmpty()) {
                throw new ValidationException("Transaction ID is required for digital payments", "TransactionId");
            }
        }

        // Validate notes length if provided
        if (notes != null && notes.length() > 1000) {
            throw new ValidationException("Payment notes cannot exceed 1000 characters", "Notes", notes);
        }
    }

    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    // Business methods
    private String generateTransactionId() {
        return paymentMethod != null ?
                paymentMethod.name() + "_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase() :
                "TXN_" + System.currentTimeMillis();
    }

    public boolean isSuccessful() {
        return status == PaymentStatus.COMPLETED;
    }

    public boolean isFailed() {
        return status == PaymentStatus.FAILED;
    }

    public boolean isPending() {
        return status == PaymentStatus.PENDING;
    }

    public boolean isProcessing() {
        return status == PaymentStatus.PROCESSING;
    }

    public boolean isRefunded() {
        return status == PaymentStatus.REFUNDED;
    }

    public boolean canProcess() {
        return status != null && status.canProcess();
    }

    public boolean isDigitalPayment() {
        return paymentMethod != null && paymentMethod.isDigital();
    }

    public boolean requiresProcessing() {
        return paymentMethod != null && paymentMethod.requiresProcessing();
    }

    public void markAsCompleted() {
        this.status = PaymentStatus.COMPLETED;
        this.paymentDate = LocalDateTime.now();
        updateTimestamp();
    }

    public void markAsFailed() {
        this.status = PaymentStatus.FAILED;
        updateTimestamp();
    }

    public void markAsProcessing() {
        this.status = PaymentStatus.PROCESSING;
        updateTimestamp();
    }

    public void markAsRefunded() {
        this.status = PaymentStatus.REFUNDED;
        updateTimestamp();
    }

    public String getFormattedAmount() {
        return amount != null ? "₹" + amount.toString() : "₹0.00";
    }

    public String getDisplayStatus() {
        return status != null ? status.getDisplayName() : "Unknown";
    }

    public String getDisplayMethod() {
        return paymentMethod != null ? paymentMethod.getDisplayName() : "Unknown";
    }

    public boolean isRecentPayment() {
        if (paymentDate == null) return false;
        return paymentDate.isAfter(LocalDateTime.now().minusHours(24));
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBillId() { return billId; }
    public void setBillId(Long billId) {
        this.billId = billId;
        updateTimestamp();
    }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
        updateTimestamp();
    }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
        // Regenerate transaction ID if method changes
        if (this.transactionId == null || this.transactionId.isEmpty()) {
            this.transactionId = generateTransactionId();
        }
        updateTimestamp();
    }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        updateTimestamp();
    }

    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
        updateTimestamp();
    }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) {
        this.status = status;
        updateTimestamp();
    }

    public String getNotes() { return notes; }
    public void setNotes(String notes) {
        this.notes = notes;
        updateTimestamp();
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Payment)) return false;
        Payment payment = (Payment) o;
        return Objects.equals(id, payment.id) &&
                Objects.equals(transactionId, payment.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, transactionId);
    }

    @Override
    public String toString() {
        return String.format("Payment{id=%d, bill=%d, amount=₹%s, method=%s, status=%s, txn=%s}",
                id, billId, amount, paymentMethod, status, transactionId);
    }
}
