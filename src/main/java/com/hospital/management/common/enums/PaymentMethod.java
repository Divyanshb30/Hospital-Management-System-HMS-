package com.hospital.management.common.enums;

/**
 * Enum representing different payment methods available
 */
public enum PaymentMethod {
    CASH("Cash", "Cash payment", true),
    CREDIT_CARD("Credit Card", "Credit card payment", true),
    DEBIT_CARD("Debit Card", "Debit card payment", true),
    UPI("UPI", "Unified Payments Interface", true),
    NET_BANKING("Net Banking", "Online banking payment", true),
    INSURANCE("Insurance", "Insurance coverage", false);

    private final String displayName;
    private final String description;
    private final boolean requiresProcessing;

    PaymentMethod(String displayName, String description, boolean requiresProcessing) {
        this.displayName = displayName;
        this.description = description;
        this.requiresProcessing = requiresProcessing;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean requiresProcessing() {
        return requiresProcessing;
    }

    public boolean isDigital() {
        return this == UPI || this == NET_BANKING || this == CREDIT_CARD || this == DEBIT_CARD;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
