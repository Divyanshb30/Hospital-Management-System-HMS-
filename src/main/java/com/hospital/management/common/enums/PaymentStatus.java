package com.hospital.management.common.enums;

/**
 * Enum representing payment status for bills
 */
public enum PaymentStatus {
    PENDING("Pending", "Payment is pending"),
    PROCESSING("Processing", "Payment is being processed"),
    COMPLETED("Completed", "Payment has been completed successfully"),
    FAILED("Failed", "Payment has failed"),
    REFUNDED("Refunded", "Payment has been refunded"),
    PARTIALLY_PAID("Partially Paid", "Bill is partially paid");

    private final String displayName;
    private final String description;

    PaymentStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPaid() {
        return this == COMPLETED || this == PARTIALLY_PAID;
    }

    public boolean canProcess() {
        return this == PENDING || this == FAILED;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
