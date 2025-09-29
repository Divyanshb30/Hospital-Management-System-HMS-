package com.hospital.management.inventory.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Equipment
 * ---------
 * Represents medical equipment in the hospital's inventory.
 * Can cover both consumable items (syringes, gloves) and durable assets (MRI, ventilators).
 */
public class Equipment {

    private Long id;
    private String name;
    private String category;            // e.g., IMAGING, SURGICAL, ICU, GENERAL
    private String modelNumber;         // Manufacturer model number
    private String serialNumber;        // Unique identifier
    private LocalDate purchaseDate;
    private LocalDate warrantyExpiry;   // For durable items
    private BigDecimal unitCost;
    private int quantityInStock;
    private Long supplierId;            // FK -> Supplier
    private String status;              // ACTIVE / IN_USE / UNDER_MAINTENANCE / DISCARDED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // -------- Constructors --------
    public Equipment() {}

    public Equipment(Long id, String name, String category, String modelNumber, String serialNumber,
                     LocalDate purchaseDate, LocalDate warrantyExpiry, BigDecimal unitCost,
                     int quantityInStock, Long supplierId, String status,
                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.modelNumber = modelNumber;
        this.serialNumber = serialNumber;
        this.purchaseDate = purchaseDate;
        this.warrantyExpiry = warrantyExpiry;
        this.unitCost = unitCost;
        this.quantityInStock = quantityInStock;
        this.supplierId = supplierId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // -------- Getters & Setters --------
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getModelNumber() { return modelNumber; }
    public void setModelNumber(String modelNumber) { this.modelNumber = modelNumber; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }

    public LocalDate getWarrantyExpiry() { return warrantyExpiry; }
    public void setWarrantyExpiry(LocalDate warrantyExpiry) { this.warrantyExpiry = warrantyExpiry; }

    public BigDecimal getUnitCost() { return unitCost; }
    public void setUnitCost(BigDecimal unitCost) { this.unitCost = unitCost; }

    public int getQuantityInStock() { return quantityInStock; }
    public void setQuantityInStock(int quantityInStock) { this.quantityInStock = quantityInStock; }

    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // -------- equals & hashCode (based on id) --------
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Equipment)) return false;
        Equipment that = (Equipment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // -------- toString --------
    @Override
    public String toString() {
        return "Equipment{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", modelNumber='" + modelNumber + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", purchaseDate=" + purchaseDate +
                ", warrantyExpiry=" + warrantyExpiry +
                ", unitCost=" + unitCost +
                ", quantityInStock=" + quantityInStock +
                ", supplierId=" + supplierId +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
