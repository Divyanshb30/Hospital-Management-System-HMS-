package com.hospital.management.inventory.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * StockAlert
 * ----------
 * Represents a low-stock (or out-of-stock) alert for an inventory item.
 * Works for both medicines and equipment via (itemType, itemId).
 *
 * Conventions:
 *  - itemType: "MEDICINE" | "EQUIPMENT"
 *  - alertLevel: "INFO" | "WARNING" | "CRITICAL"
 *  - status: "OPEN" | "ACKNOWLEDGED" | "RESOLVED"
 */
public class StockAlert {

    private Long id;                    // Unique identifier
    private String itemType;            // MEDICINE / EQUIPMENT
    private Long itemId;                // FK to Medicine.id or Equipment.id
    private String itemName;            // Cached name for quick display (optional)

    private int threshold;              // Trigger level (e.g., alert if qty <= threshold)
    private int currentQuantity;        // Quantity when alert was created

    private String alertLevel;          // INFO / WARNING / CRITICAL
    private String status;              // OPEN / ACKNOWLEDGED / RESOLVED
    private String message;             // Human-readable message

    private LocalDateTime createdAt;    // When the alert was created
    private LocalDateTime acknowledgedAt; // When someone acknowledged it (optional)
    private LocalDateTime resolvedAt;   // When stock was replenished / alert closed
    private LocalDateTime lastNotifiedAt; // For notification throttling (optional)

    // -------- Constructors --------
    public StockAlert() {}

    public StockAlert(Long id, String itemType, Long itemId, String itemName,
                      int threshold, int currentQuantity, String alertLevel,
                      String status, String message,
                      LocalDateTime createdAt, LocalDateTime acknowledgedAt,
                      LocalDateTime resolvedAt, LocalDateTime lastNotifiedAt) {
        this.id = id;
        this.itemType = itemType;
        this.itemId = itemId;
        this.itemName = itemName;
        this.threshold = threshold;
        this.currentQuantity = currentQuantity;
        this.alertLevel = alertLevel;
        this.status = status;
        this.message = message;
        this.createdAt = createdAt;
        this.acknowledgedAt = acknowledgedAt;
        this.resolvedAt = resolvedAt;
        this.lastNotifiedAt = lastNotifiedAt;
    }

    // -------- Getters & Setters --------
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public int getThreshold() { return threshold; }
    public void setThreshold(int threshold) { this.threshold = threshold; }

    public int getCurrentQuantity() { return currentQuantity; }
    public void setCurrentQuantity(int currentQuantity) { this.currentQuantity = currentQuantity; }

    public String getAlertLevel() { return alertLevel; }
    public void setAlertLevel(String alertLevel) { this.alertLevel = alertLevel; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getAcknowledgedAt() { return acknowledgedAt; }
    public void setAcknowledgedAt(LocalDateTime acknowledgedAt) { this.acknowledgedAt = acknowledgedAt; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    public LocalDateTime getLastNotifiedAt() { return lastNotifiedAt; }
    public void setLastNotifiedAt(LocalDateTime lastNotifiedAt) { this.lastNotifiedAt = lastNotifiedAt; }

    // -------- equals & hashCode (by id) --------
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StockAlert)) return false;
        StockAlert that = (StockAlert) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    // -------- toString --------
    @Override
    public String toString() {
        return "StockAlert{" +
                "id=" + id +
                ", itemType='" + itemType + '\'' +
                ", itemId=" + itemId +
                ", itemName='" + itemName + '\'' +
                ", threshold=" + threshold +
                ", currentQuantity=" + currentQuantity +
                ", alertLevel='" + alertLevel + '\'' +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", createdAt=" + createdAt +
                ", acknowledgedAt=" + acknowledgedAt +
                ", resolvedAt=" + resolvedAt +
                ", lastNotifiedAt=" + lastNotifiedAt +
                '}';
    }
}
