package com.hospital.management.models;

import com.hospital.management.common.enums.PaymentStatus;
import com.hospital.management.common.exceptions.ValidationException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Bill model representing financial billing for appointments
 */
public class Bill {

    private Long id;
    private Long appointmentId;
    private Long patientId;
    private BigDecimal totalAmount;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private PaymentStatus status;
    private LocalDate billDate;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor
    public Bill() {
        this.status = PaymentStatus.PENDING;
        this.taxAmount = BigDecimal.ZERO;
        this.discountAmount = BigDecimal.ZERO;
        this.billDate = LocalDate.now();
        this.dueDate = LocalDate.now().plusDays(30); // 30 days to pay
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor with essential fields
    public Bill(Long appointmentId, Long patientId, BigDecimal totalAmount) {
        this();
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.totalAmount = totalAmount;
        this.finalAmount = calculateFinalAmount();
    }

    // Constructor with tax and discount
    public Bill(Long appointmentId, Long patientId, BigDecimal totalAmount,
                BigDecimal taxAmount, BigDecimal discountAmount) {
        this();
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.totalAmount = totalAmount;
        this.taxAmount = taxAmount != null ? taxAmount : BigDecimal.ZERO;
        this.discountAmount = discountAmount != null ? discountAmount : BigDecimal.ZERO;
        this.finalAmount = calculateFinalAmount();
    }

    // Full constructor
    public Bill(Long id, Long appointmentId, Long patientId, BigDecimal totalAmount,
                BigDecimal taxAmount, BigDecimal discountAmount, BigDecimal finalAmount,
                PaymentStatus status, LocalDate billDate, LocalDate dueDate,
                LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.totalAmount = totalAmount;
        this.taxAmount = taxAmount;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.status = status;
        this.billDate = billDate;
        this.dueDate = dueDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Validates the bill object
     */
    public void validate() throws ValidationException {
        if (appointmentId == null) {
            throw new ValidationException("Appointment ID is required", "AppointmentId");
        }

        if (patientId == null) {
            throw new ValidationException("Patient ID is required", "PatientId");
        }

        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Total amount must be non-negative", "TotalAmount", totalAmount);
        }

        if (taxAmount == null || taxAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Tax amount must be non-negative", "TaxAmount", taxAmount);
        }

        if (discountAmount == null || discountAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Discount amount must be non-negative", "DiscountAmount", discountAmount);
        }

        if (discountAmount.compareTo(totalAmount) > 0) {
            throw new ValidationException("Discount cannot exceed total amount", "DiscountAmount", discountAmount);
        }

        if (billDate == null) {
            throw new ValidationException("Bill date is required", "BillDate");
        }

        if (dueDate == null) {
            throw new ValidationException("Due date is required", "DueDate");
        }

        if (dueDate.isBefore(billDate)) {
            throw new ValidationException("Due date cannot be before bill date", "DueDate", dueDate);
        }

        if (status == null) {
            throw new ValidationException("Payment status is required", "Status");
        }
    }

    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    // Business methods
    public BigDecimal calculateFinalAmount() {
        if (totalAmount == null) return BigDecimal.ZERO;

        BigDecimal tax = taxAmount != null ? taxAmount : BigDecimal.ZERO;
        BigDecimal discount = discountAmount != null ? discountAmount : BigDecimal.ZERO;

        return totalAmount.add(tax).subtract(discount).setScale(2, RoundingMode.HALF_UP);
    }

    public void recalculateFinalAmount() {
        this.finalAmount = calculateFinalAmount();
        updateTimestamp();
    }

    public boolean isPaid() {
        return status != null && status.isPaid();
    }

    public boolean isPending() {
        return status == PaymentStatus.PENDING;
    }

    public boolean isOverdue() {
        return dueDate != null && dueDate.isBefore(LocalDate.now()) && !isPaid();
    }

    public long getDaysUntilDue() {
        if (dueDate == null) return 0;
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
    }

    public boolean canProcess() {
        return status != null && status.canProcess();
    }

    public BigDecimal getNetAmount() {
        if (totalAmount == null || discountAmount == null) return BigDecimal.ZERO;
        return totalAmount.subtract(discountAmount);
    }

    public BigDecimal getTaxRate() {
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) == 0 || taxAmount == null) {
            return BigDecimal.ZERO;
        }
        return taxAmount.divide(totalAmount, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
    }

    public BigDecimal getDiscountRate() {
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) == 0 || discountAmount == null) {
            return BigDecimal.ZERO;
        }
        return discountAmount.divide(totalAmount, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
    }

    public void applyDiscount(BigDecimal discountPercent) {
        if (totalAmount != null && discountPercent != null) {
            BigDecimal discount = totalAmount.multiply(discountPercent.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
            setDiscountAmount(discount);
        }
    }

    public void applyTax(BigDecimal taxPercent) {
        if (totalAmount != null && taxPercent != null) {
            BigDecimal tax = totalAmount.multiply(taxPercent.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
            setTaxAmount(tax);
        }
    }

    public void markAsPaid() {
        this.status = PaymentStatus.COMPLETED;
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

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
        updateTimestamp();
    }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) {
        this.patientId = patientId;
        updateTimestamp();
    }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
        recalculateFinalAmount();
    }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
        recalculateFinalAmount();
    }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
        recalculateFinalAmount();
    }

    public BigDecimal getFinalAmount() { return finalAmount; }
    public void setFinalAmount(BigDecimal finalAmount) {
        this.finalAmount = finalAmount;
        updateTimestamp();
    }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) {
        this.status = status;
        updateTimestamp();
    }

    public LocalDate getBillDate() { return billDate; }
    public void setBillDate(LocalDate billDate) {
        this.billDate = billDate;
        updateTimestamp();
    }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
        updateTimestamp();
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bill)) return false;
        Bill bill = (Bill) o;
        return Objects.equals(id, bill.id) &&
                Objects.equals(appointmentId, bill.appointmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, appointmentId);
    }

    @Override
    public String toString() {
        return String.format("Bill{id=%d, appointment=%d, patient=%d, amount=₹%s, status=%s}",
                id, appointmentId, patientId, finalAmount, status);
    }
}
