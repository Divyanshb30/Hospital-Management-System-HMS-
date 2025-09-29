package com.hospital.management.billing.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Insurance
 * ---------
 * Represents an insurance claim for a patient's bill.
 * Tracks insurer details, claim status, and settlement amounts.
 */
public class Insurance {

    private Long id;                        // Unique identifier for insurance record
    private Long patientId;                 // Linked Patient
    private Long billId;                    // Linked Bill
    private String insuranceProvider;       // e.g., "Blue Cross", "Aetna"
    private String policyNumber;            // Patient’s insurance policy number
    private BigDecimal claimAmount;         // Amount claimed from insurer
    private BigDecimal approvedAmount;      // Amount approved by insurer
    private String status;                  // SUBMITTED, APPROVED, REJECTED, PENDING, SETTLED
    private String remarks;                 // Notes (e.g., rejection reason)
    private LocalDateTime submittedAt;      // When claim was submitted
    private LocalDateTime settledAt;        // When claim was settled
    private LocalDateTime createdAt;        // DB audit
    private LocalDateTime updatedAt;        // DB audit

    // --- Constructors ---
    public Insurance() {}

    public Insurance(Long id, Long patientId, Long billId,
                     String insuranceProvider, String policyNumber,
                     BigDecimal claimAmount, BigDecimal approvedAmount,
                     String status, String remarks,
                     LocalDateTime submittedAt, LocalDateTime settledAt,
                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.patientId = patientId;
        this.billId = billId;
        this.insuranceProvider = insuranceProvider;
        this.policyNumber = policyNumber;
        this.claimAmount = claimAmount;
        this.approvedAmount = approvedAmount;
        this.status = status;
        this.remarks = remarks;
        this.submittedAt = submittedAt;
        this.settledAt = settledAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public Long getBillId() { return billId; }
    public void setBillId(Long billId) { this.billId = billId; }

    public String getInsuranceProvider() { return insuranceProvider; }
    public void setInsuranceProvider(String insuranceProvider) { this.insuranceProvider = insuranceProvider; }

    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }

    public BigDecimal getClaimAmount() { return claimAmount; }
    public void setClaimAmount(BigDecimal claimAmount) { this.claimAmount = claimAmount; }

    public BigDecimal getApprovedAmount() { return approvedAmount; }
    public void setApprovedAmount(BigDecimal approvedAmount) { this.approvedAmount = approvedAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public LocalDateTime getSettledAt() { return settledAt; }
    public void setSettledAt(LocalDateTime settledAt) { this.settledAt = settledAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // --- Utility Methods ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Insurance)) return false;
        Insurance that = (Insurance) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Insurance{" +
                "id=" + id +
                ", patientId=" + patientId +
                ", billId=" + billId +
                ", insuranceProvider='" + insuranceProvider + '\'' +
                ", policyNumber='" + policyNumber + '\'' +
                ", claimAmount=" + claimAmount +
                ", approvedAmount=" + approvedAmount +
                ", status='" + status + '\'' +
                ", remarks='" + remarks + '\'' +
                ", submittedAt=" + submittedAt +
                ", settledAt=" + settledAt +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
