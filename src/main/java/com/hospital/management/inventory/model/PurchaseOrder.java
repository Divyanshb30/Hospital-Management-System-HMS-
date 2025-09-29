package com.hospital.management.inventory.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * PurchaseOrder
 * -------------
 * Represents a purchase order raised to suppliers for medicines/equipment.
 * Tracks approval workflow, delivery, and payment status.
 */
public class PurchaseOrder {

    private Long id;
    private Long supplierId;         // FK -> Supplier
    private String itemType;         // MEDICINE / EQUIPMENT
    private Long itemId;             // FK -> Medicine.id or Equipment.id
    private String itemName;         // Cached for quick reference

    private int quantityOrdered;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;

    private LocalDate orderDate;
    private LocalDate expectedDeliveryDate;
    private LocalDate actualDeliveryDate;

    private String status;           // PENDING / APPROVED / REJECTED / DISPATCHED / RECEIVED / CANCELLED
    private String paymentStatus;    // UNPAID / PARTIAL / PAID

    private String remarks;          // Optional notes (urgent, replacement, etc.)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // -------- Constructors --------
    public PurchaseOrder() {}

    public PurchaseOrder(Long id, Long supplierId, String itemType, Long itemId, String itemName,
                         int quantityOrdered, BigDecimal unitPrice, BigDecimal totalAmount,
                         LocalDate orderDate, LocalDate expectedDeliveryDate, LocalDate actualDeliveryDate,
                         String status, String paymentStatus, String remarks,
                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.supplierId = supplierId;
        this.itemType = itemType;
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantityOrdered = quantityOrdered;
        this.unitPrice = unitPrice;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.actualDeliveryDate = actualDeliveryDate;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.remarks = remarks;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // -------- Getters & Setters --------
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }

    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public int getQuantityOrdered() { return quantityOrdered; }
    public void setQuantityOrdered(int quantityOrdered) { this.quantityOrdered = quantityOrdered; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }

    public LocalDate getExpectedDeliveryDate() { return expectedDeliveryDate; }
    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) { this.expectedDeliveryDate = expectedDeliveryDate; }

    public LocalDate getActualDeliveryDate() { return actualDeliveryDate; }
    public void setActualDeliveryDate(LocalDate actualDeliveryDate) { this.actualDeliveryDate = actualDeliveryDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // -------- equals & hashCode (based on id) --------
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PurchaseOrder)) return false;
        PurchaseOrder that = (PurchaseOrder) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    // -------- toString --------
    @Override
    public String toString() {
        return "PurchaseOrder{" +
                "id=" + id +
                ", supplierId=" + supplierId +
                ", itemType='" + itemType + '\'' +
                ", itemId=" + itemId +
                ", itemName='" + itemName + '\'' +
                ", quantityOrdered=" + quantityOrdered +
                ", unitPrice=" + unitPrice +
                ", totalAmount=" + totalAmount +
                ", orderDate=" + orderDate +
                ", expectedDeliveryDate=" + expectedDeliveryDate +
                ", actualDeliveryDate=" + actualDeliveryDate +
                ", status='" + status + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", remarks='" + remarks + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
