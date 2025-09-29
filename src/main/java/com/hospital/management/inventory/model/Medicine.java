package com.hospital.management.inventory.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Medicine
 * --------
 * Represents a medicine item in the hospital inventory.
 * Tracks batch info, expiry, stock levels, pricing, and audit fields.
 */
public class Medicine {

    private Long id;
    private String name;
    private String batchNumber;
    private LocalDate expiryDate;
    private Long supplierId;             // FK -> Supplier
    private int quantityInStock;
    private BigDecimal unitPrice;
    private String status;               // ACTIVE / EXPIRED / OUT_OF_STOCK
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // -------- Constructors --------
    public Medicine() {}

    public Medicine(Long id, String name, String batchNumber, LocalDate expiryDate,
                    Long supplierId, int quantityInStock, BigDecimal unitPrice,
                    String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.batchNumber = batchNumber;
        this.expiryDate = expiryDate;
        this.supplierId = supplierId;
        this.quantityInStock = quantityInStock;
        this.unitPrice = unitPrice;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // -------- Getters & Setters --------
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }

    public int getQuantityInStock() { return quantityInStock; }
    public void setQuantityInStock(int quantityInStock) { this.quantityInStock = quantityInStock; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

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
        if (!(o instanceof Medicine)) return false;
        Medicine that = (Medicine) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // -------- toString --------
    @Override
    public String toString() {
        return "Medicine{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", batchNumber='" + batchNumber + '\'' +
                ", expiryDate=" + expiryDate +
                ", supplierId=" + supplierId +
                ", quantityInStock=" + quantityInStock +
                ", unitPrice=" + unitPrice +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
