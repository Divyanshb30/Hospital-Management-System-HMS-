package com.hospital.management.inventory.service;

import com.hospital.management.inventory.model.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * InventoryService
 * ----------------
 * Business facade for Inventory workflows.
 * Applies validation, cross-entity rules, and orchestrates DAO calls.
 *
 * Typical rules:
 *  - Prevent negative stock on consume()
 *  - Auto-create/close StockAlerts around thresholds
 *  - When receiving a PurchaseOrder, update stock + statuses
 *  - Flag medicines nearing expiry & equipment warranties nearing expiry
 */
public interface InventoryService {

    // ======================= MEDICINES =======================

    Long addMedicine(Medicine medicine);

    Optional<Medicine> getMedicine(Long id);

    List<Medicine> searchMedicinesByName(String name);

    /** Medicines with expiry <= today + days. */
    List<Medicine> medicinesExpiringWithin(int days);

    List<Medicine> listAllMedicines();

    boolean updateMedicine(Medicine medicine);

    /** Increase stock (e.g., after receiving PO); may close low-stock alerts. */
    boolean restockMedicine(Long medicineId, int quantityToAdd);

    /**
     * Decrease stock (e.g., dispensing). Must not go negative.
     * If newQty <= threshold (if you store per-item thresholds), create LOW-STOCK alert.
     */
    boolean consumeMedicine(Long medicineId, int quantityToConsume);

    boolean removeMedicine(Long medicineId);


    // ======================= EQUIPMENT =======================

    Long addEquipment(Equipment equipment);

    Optional<Equipment> getEquipment(Long id);

    List<Equipment> listEquipmentByCategory(String category);

    List<Equipment> listEquipmentUnderMaintenance();

    List<Equipment> listAllEquipment();

    boolean updateEquipment(Equipment equipment);

    boolean setEquipmentStatus(Long equipmentId, String status); // ACTIVE / IN_USE / UNDER_MAINTENANCE / DISCARDED

    boolean removeEquipment(Long equipmentId);

    /** Equipment whose warranty expires on or before cutoffDate. */
    List<Equipment> equipmentWarrantyExpiringBy(LocalDate cutoffDate);


    // ======================= SUPPLIERS =======================

    Long addSupplier(Supplier supplier);

    Optional<Supplier> getSupplier(Long id);

    List<Supplier> searchSuppliersByName(String name);

    List<Supplier> listSuppliersByStatus(String status);

    List<Supplier> listAllSuppliers();

    boolean updateSupplier(Supplier supplier);

    boolean removeSupplier(Long supplierId);


    // ======================= STOCK ALERTS =======================

    Long openStockAlert(StockAlert alert);

    Optional<StockAlert> getStockAlert(Long id);

    List<StockAlert> listAlertsByStatus(String status); // OPEN / ACKNOWLEDGED / RESOLVED

    List<StockAlert> listAlertsForItem(Long itemId, String itemType); // MEDICINE / EQUIPMENT

    List<StockAlert> listAllAlerts();

    /** Update details or timestamps (ack/resolution handled by the helpers below). */
    boolean updateStockAlert(StockAlert alert);

    /** Mark alert as ACKNOWLEDGED and set acknowledgedAt now. */
    boolean acknowledgeAlert(Long alertId);

    /** Mark alert as RESOLVED and set resolvedAt now. */
    boolean resolveAlert(Long alertId);

    boolean deleteAlert(Long alertId);


    // ======================= PURCHASE ORDERS =======================

    Long createPurchaseOrder(PurchaseOrder po);

    Optional<PurchaseOrder> getPurchaseOrder(Long id);

    List<PurchaseOrder> listOrdersBySupplier(Long supplierId);

    List<PurchaseOrder> listOrdersByStatus(String status);

    List<PurchaseOrder> listAllPurchaseOrders();

    boolean updatePurchaseOrder(PurchaseOrder po);

    boolean setPurchaseOrderStatus(Long poId, String status); // PENDING / APPROVED / DISPATCHED / RECEIVED / ...

    boolean deletePurchaseOrder(Long poId);

    /**
     * Receive a PurchaseOrder:
     *  - set status to RECEIVED, set actualDeliveryDate (if provided)
     *  - restock the referenced item (medicine/equipment) by quantityOrdered
     *  - close matching OPEN low-stock alerts for that item
     */
    boolean receivePurchaseOrder(Long poId, LocalDate actualDeliveryDate);


    // ======================= UTILITIES / ORCHESTRATIONS =======================

    /**
     * Ensures alerts exist for any items below their threshold.
     * If thresholds are stored externally, pass a defaultThreshold.
     */
    int ensureLowStockAlertsForAll(int defaultThreshold);

    /**
     * Clears (resolves) OPEN alerts for items whose stock is now above threshold.
     */
    int resolveRecoveredStockAlerts(int defaultThreshold);


}
