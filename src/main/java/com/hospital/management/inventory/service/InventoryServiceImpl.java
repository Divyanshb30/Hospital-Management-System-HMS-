package com.hospital.management.inventory.service;

import com.hospital.management.inventory.dao.InventoryDAO;
import com.hospital.management.inventory.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * InventoryServiceImpl
 * --------------------
 * Orchestrates inventory workflows and applies business rules,
 * delegating persistence to InventoryDAO.
 *
 * NOTE: If you add a transaction manager later, wrap multi-step
 * operations (like receivePurchaseOrder) in a transaction.
 */
public class InventoryServiceImpl implements InventoryService {

    private final InventoryDAO dao;

    // You can later make these configurable (from properties/db).
    private final int defaultLowStockThreshold;

    public InventoryServiceImpl(InventoryDAO dao) {
        this(dao, 10); // default threshold: 10 units
    }

    public InventoryServiceImpl(InventoryDAO dao, int defaultLowStockThreshold) {
        this.dao = Objects.requireNonNull(dao, "dao must not be null");
        this.defaultLowStockThreshold = Math.max(0, defaultLowStockThreshold);
    }

    // ======================= MEDICINES =======================

    @Override
    public Long addMedicine(Medicine medicine) {
        requireNonBlank(medicine.getName(), "medicine.name");
        if (medicine.getStatus() == null || medicine.getStatus().isBlank()) medicine.setStatus("ACTIVE");
        if (medicine.getCreatedAt() == null) medicine.setCreatedAt(LocalDateTime.now());
        if (medicine.getUpdatedAt() == null) medicine.setUpdatedAt(LocalDateTime.now());
        return dao.insertMedicine(medicine);
    }

    @Override
    public Optional<Medicine> getMedicine(Long id) {
        requireId(id, "medicineId");
        return dao.findMedicineById(id);
    }

    @Override
    public List<Medicine> searchMedicinesByName(String name) {
        return dao.findMedicinesByName(name);
    }

    @Override
    public List<Medicine> medicinesExpiringWithin(int days) {
        return dao.findMedicinesExpiringSoon(days);
    }

    @Override
    public List<Medicine> listAllMedicines() {
        return dao.findAllMedicines();
    }

    @Override
    public boolean updateMedicine(Medicine medicine) {
        requireId(medicine.getId(), "medicine.id");
        medicine.setUpdatedAt(LocalDateTime.now());
        return dao.updateMedicine(medicine);
    }

    @Override
    public boolean restockMedicine(Long medicineId, int quantityToAdd) {
        requireId(medicineId, "medicineId");
        requireNonNegative(quantityToAdd, "quantityToAdd");

        Medicine med = dao.findMedicineById(medicineId)
                .orElseThrow(() -> new IllegalArgumentException("Medicine not found: " + medicineId));

        int newQty = Math.addExact(med.getQuantityInStock(), quantityToAdd);
        boolean ok = dao.updateMedicineStock(medicineId, newQty);

        // If stock recovers above threshold, resolve OPEN alerts for this item.
        if (ok && newQty > thresholdFor(med)) {
            resolveAlertsForItem(medicineId, "MEDICINE");
        }
        return ok;
    }

    @Override
    public boolean consumeMedicine(Long medicineId, int quantityToConsume) {
        requireId(medicineId, "medicineId");
        requirePositive(quantityToConsume, "quantityToConsume");

        Medicine med = dao.findMedicineById(medicineId)
                .orElseThrow(() -> new IllegalArgumentException("Medicine not found: " + medicineId));

        int current = med.getQuantityInStock();
        if (quantityToConsume > current) {
            throw new IllegalArgumentException("Insufficient stock: have " + current + ", need " + quantityToConsume);
        }

        int newQty = current - quantityToConsume;
        boolean ok = dao.updateMedicineStock(medicineId, newQty);

        // If below/equal threshold, open (or ensure) a low-stock alert
        if (ok && newQty <= thresholdFor(med)) {
            ensureOpenAlertForItem(medicineId, "MEDICINE", med.getName(), newQty, thresholdFor(med));
        }
        return ok;
    }

    @Override
    public boolean removeMedicine(Long medicineId) {
        requireId(medicineId, "medicineId");
        return dao.deleteMedicine(medicineId);
    }

    // ======================= EQUIPMENT =======================

    @Override
    public Long addEquipment(Equipment equipment) {
        requireNonBlank(equipment.getName(), "equipment.name");
        if (equipment.getStatus() == null || equipment.getStatus().isBlank()) equipment.setStatus("ACTIVE");
        if (equipment.getCreatedAt() == null) equipment.setCreatedAt(LocalDateTime.now());
        if (equipment.getUpdatedAt() == null) equipment.setUpdatedAt(LocalDateTime.now());
        return dao.insertEquipment(equipment);
    }

    @Override
    public Optional<Equipment> getEquipment(Long id) {
        requireId(id, "equipmentId");
        return dao.findEquipmentById(id);
    }

    @Override
    public List<Equipment> listEquipmentByCategory(String category) {
        requireNonBlank(category, "category");
        return dao.findEquipmentByCategory(category);
    }

    @Override
    public List<Equipment> listEquipmentUnderMaintenance() {
        return dao.findEquipmentUnderMaintenance();
    }

    @Override
    public List<Equipment> listAllEquipment() {
        return dao.findAllEquipment();
    }

    @Override
    public boolean updateEquipment(Equipment equipment) {
        requireId(equipment.getId(), "equipment.id");
        equipment.setUpdatedAt(LocalDateTime.now());
        return dao.updateEquipment(equipment);
    }

    @Override
    public boolean setEquipmentStatus(Long equipmentId, String status) {
        requireId(equipmentId, "equipmentId");
        requireNonBlank(status, "status");
        return dao.updateEquipmentStatus(equipmentId, status);
    }

    @Override
    public boolean removeEquipment(Long equipmentId) {
        requireId(equipmentId, "equipmentId");
        return dao.deleteEquipment(equipmentId);
    }

    @Override
    public List<Equipment> equipmentWarrantyExpiringBy(LocalDate cutoffDate) {
        Objects.requireNonNull(cutoffDate, "cutoffDate must not be null");
        return dao.findAllEquipment().stream()
                .filter(e -> e.getWarrantyExpiry() != null && !e.getWarrantyExpiry().isAfter(cutoffDate))
                .sorted(Comparator.comparing(Equipment::getWarrantyExpiry))
                .collect(Collectors.toList());
    }

    // ======================= SUPPLIERS =======================

    @Override
    public Long addSupplier(Supplier supplier) {
        requireNonBlank(supplier.getName(), "supplier.name");
        if (supplier.getStatus() == null || supplier.getStatus().isBlank()) supplier.setStatus("ACTIVE");
        if (supplier.getCreatedAt() == null) supplier.setCreatedAt(LocalDateTime.now());
        if (supplier.getUpdatedAt() == null) supplier.setUpdatedAt(LocalDateTime.now());
        return dao.insertSupplier(supplier);
    }

    @Override
    public Optional<Supplier> getSupplier(Long id) {
        requireId(id, "supplierId");
        return dao.findSupplierById(id);
    }

    @Override
    public List<Supplier> searchSuppliersByName(String name) {
        return dao.findSuppliersByName(name);
    }

    @Override
    public List<Supplier> listSuppliersByStatus(String status) {
        requireNonBlank(status, "status");
        return dao.findSuppliersByStatus(status);
    }

    @Override
    public List<Supplier> listAllSuppliers() {
        return dao.findAllSuppliers();
    }

    @Override
    public boolean updateSupplier(Supplier supplier) {
        requireId(supplier.getId(), "supplier.id");
        supplier.setUpdatedAt(LocalDateTime.now());
        return dao.updateSupplier(supplier);
    }

    @Override
    public boolean removeSupplier(Long supplierId) {
        requireId(supplierId, "supplierId");
        return dao.deleteSupplier(supplierId);
    }

    // ======================= STOCK ALERTS =======================

    @Override
    public Long openStockAlert(StockAlert alert) {
        normalizeAlert(alert);
        return dao.insertStockAlert(alert);
    }

    @Override
    public Optional<StockAlert> getStockAlert(Long id) {
        requireId(id, "alertId");
        return dao.findAlertById(id);
    }

    @Override
    public List<StockAlert> listAlertsByStatus(String status) {
        requireNonBlank(status, "status");
        return dao.findAlertsByStatus(status);
    }

    @Override
    public List<StockAlert> listAlertsForItem(Long itemId, String itemType) {
        requireId(itemId, "itemId");
        requireNonBlank(itemType, "itemType");
        return dao.findAlertsByItem(itemId, itemType);
    }

    @Override
    public List<StockAlert> listAllAlerts() {
        return dao.findAllAlerts();
    }

    @Override
    public boolean updateStockAlert(StockAlert alert) {
        requireId(alert.getId(), "alert.id");
        return dao.updateStockAlert(alert);
    }

    @Override
    public boolean acknowledgeAlert(Long alertId) {
        requireId(alertId, "alertId");
        StockAlert alert = dao.findAlertById(alertId)
                .orElseThrow(() -> new IllegalArgumentException("Alert not found: " + alertId));
        alert.setStatus("ACKNOWLEDGED");
        alert.setAcknowledgedAt(LocalDateTime.now());
        return dao.updateStockAlert(alert);
    }

    @Override
    public boolean resolveAlert(Long alertId) {
        requireId(alertId, "alertId");
        // Use dedicated shortcut to set RESOLVED + timestamp
        return dao.closeStockAlert(alertId);
    }

    @Override
    public boolean deleteAlert(Long alertId) {
        requireId(alertId, "alertId");
        return dao.deleteStockAlert(alertId);
    }

    // ======================= PURCHASE ORDERS =======================

    @Override
    public Long createPurchaseOrder(PurchaseOrder po) {
        requireId(po.getSupplierId(), "po.supplierId");
        requireNonBlank(po.getItemType(), "po.itemType");
        requireId(po.getItemId(), "po.itemId");
        if (po.getStatus() == null || po.getStatus().isBlank()) po.setStatus("PENDING");
        if (po.getPaymentStatus() == null || po.getPaymentStatus().isBlank()) po.setPaymentStatus("UNPAID");
        if (po.getOrderDate() == null) po.setOrderDate(LocalDate.now());
        if (po.getCreatedAt() == null) po.setCreatedAt(LocalDateTime.now());
        if (po.getUpdatedAt() == null) po.setUpdatedAt(LocalDateTime.now());
        return dao.insertPurchaseOrder(po);
    }

    @Override
    public Optional<PurchaseOrder> getPurchaseOrder(Long id) {
        requireId(id, "poId");
        return dao.findPurchaseOrderById(id);
    }

    @Override
    public List<PurchaseOrder> listOrdersBySupplier(Long supplierId) {
        requireId(supplierId, "supplierId");
        return dao.findOrdersBySupplier(supplierId);
    }

    @Override
    public List<PurchaseOrder> listOrdersByStatus(String status) {
        requireNonBlank(status, "status");
        return dao.findOrdersByStatus(status);
    }

    @Override
    public List<PurchaseOrder> listAllPurchaseOrders() {
        return dao.findAllPurchaseOrders();
    }

    @Override
    public boolean updatePurchaseOrder(PurchaseOrder po) {
        requireId(po.getId(), "po.id");
        po.setUpdatedAt(LocalDateTime.now());
        return dao.updatePurchaseOrder(po);
    }

    @Override
    public boolean setPurchaseOrderStatus(Long poId, String status) {
        requireId(poId, "poId");
        requireNonBlank(status, "status");
        return dao.updatePurchaseOrderStatus(poId, status);
    }

    @Override
    public boolean deletePurchaseOrder(Long poId) {
        requireId(poId, "poId");
        return dao.deletePurchaseOrder(poId);
    }

    @Override
    public boolean receivePurchaseOrder(Long poId, LocalDate actualDeliveryDate) {
        requireId(poId, "poId");
        PurchaseOrder po = dao.findPurchaseOrderById(poId)
                .orElseThrow(() -> new IllegalArgumentException("PO not found: " + poId));

        // 1) Mark as RECEIVED + delivery date
        po.setStatus("RECEIVED");
        po.setActualDeliveryDate(actualDeliveryDate != null ? actualDeliveryDate : LocalDate.now());
        po.setUpdatedAt(LocalDateTime.now());
        boolean ok = dao.updatePurchaseOrder(po);
        if (!ok) return false;

        // 2) Restock the referenced item
        if ("MEDICINE".equalsIgnoreCase(po.getItemType())) {
            dao.findMedicineById(po.getItemId()).ifPresent(med -> {
                int newQty = Math.addExact(med.getQuantityInStock(), po.getQuantityOrdered());
                dao.updateMedicineStock(med.getId(), newQty);
                if (newQty > thresholdFor(med)) resolveAlertsForItem(med.getId(), "MEDICINE");
            });
        } else if ("EQUIPMENT".equalsIgnoreCase(po.getItemType())) {
            dao.findEquipmentById(po.getItemId()).ifPresent(eq -> {
                // For consumable equipment (e.g., gloves), quantityInStock matters.
                int newQty = Math.addExact(eq.getQuantityInStock(), po.getQuantityOrdered());
                eq.setQuantityInStock(newQty);
                eq.setUpdatedAt(LocalDateTime.now());
                dao.updateEquipment(eq);
                if (newQty > defaultLowStockThreshold) resolveAlertsForItem(eq.getId(), "EQUIPMENT");
            });
        }

        return true;
    }

    // ======================= UTILITIES / ORCHESTRATIONS =======================

    @Override
    public int ensureLowStockAlertsForAll(int defaultThreshold) {
        int created = 0;

        // Check medicines
        for (Medicine m : dao.findAllMedicines()) {
            int threshold = thresholdFor(m, defaultThreshold);
            if (m.getQuantityInStock() <= threshold) {
                created += ensureOpenAlertForItem(m.getId(), "MEDICINE", m.getName(), m.getQuantityInStock(), threshold) ? 1 : 0;
            }
        }

        // Check equipment (only if you treat as consumable)
        for (Equipment e : dao.findAllEquipment()) {
            int threshold = defaultThreshold; // simple rule for equipment
            if (e.getQuantityInStock() <= threshold) {
                created += ensureOpenAlertForItem(e.getId(), "EQUIPMENT", e.getName(), e.getQuantityInStock(), threshold) ? 1 : 0;
            }
        }

        return created;
    }

    @Override
    public int resolveRecoveredStockAlerts(int defaultThreshold) {
        int resolved = 0;

        // Medicines
        for (Medicine m : dao.findAllMedicines()) {
            int threshold = thresholdFor(m, defaultThreshold);
            if (m.getQuantityInStock() > threshold) {
                resolved += resolveAlertsForItem(m.getId(), "MEDICINE");
            }
        }

        // Equipment
        for (Equipment e : dao.findAllEquipment()) {
            if (e.getQuantityInStock() > defaultThreshold) {
                resolved += resolveAlertsForItem(e.getId(), "EQUIPMENT");
            }
        }

        return resolved;
    }

    // ======================= Private helpers =======================

    private int thresholdFor(Medicine med) {
        return thresholdFor(med, defaultLowStockThreshold);
    }

    private int thresholdFor(Medicine med, int fallback) {
        // If you later add per-item threshold in DB/properties, read it here.
        return Math.max(0, fallback);
    }

    private boolean ensureOpenAlertForItem(Long itemId, String itemType, String itemName, int currentQty, int threshold) {
        // If there's already an OPEN alert for this item, just update message/qty; else create new.
        List<StockAlert> existing = dao.findAlertsByItem(itemId, itemType).stream()
                .filter(a -> "OPEN".equalsIgnoreCase(a.getStatus()) || "ACKNOWLEDGED".equalsIgnoreCase(a.getStatus()))
                .collect(Collectors.toList());

        String level = (currentQty <= 0) ? "CRITICAL" : (currentQty <= Math.max(1, threshold / 2) ? "WARNING" : "INFO");
        String msg = String.format("%s low stock: qty=%d, threshold=%d", itemName, currentQty, threshold);

        if (existing.isEmpty()) {
            StockAlert alert = new StockAlert(
                    null, itemType, itemId, itemName,
                    threshold, currentQty, level,
                    "OPEN", msg,
                    LocalDateTime.now(), null, null, null
            );
            return dao.insertStockAlert(alert) != null;
        } else {
            // Update first open/ack alert
            StockAlert alert = existing.get(0);
            alert.setCurrentQuantity(currentQty);
            alert.setAlertLevel(level);
            alert.setMessage(msg);
            if (!"OPEN".equalsIgnoreCase(alert.getStatus())) {
                alert.setStatus("OPEN"); // re-open if previously acknowledged
            }
            return dao.updateStockAlert(alert);
        }
    }

    private int resolveAlertsForItem(Long itemId, String itemType) {
        int count = 0;
        for (StockAlert a : dao.findAlertsByItem(itemId, itemType)) {
            if (!"RESOLVED".equalsIgnoreCase(a.getStatus())) {
                if (dao.closeStockAlert(a.getId())) count++;
            }
        }
        return count;
    }

    private void normalizeAlert(StockAlert alert) {
        Objects.requireNonNull(alert, "alert must not be null");
        requireNonBlank(alert.getItemType(), "alert.itemType");
        requireId(alert.getItemId(), "alert.itemId");
        if (alert.getStatus() == null || alert.getStatus().isBlank()) alert.setStatus("OPEN");
        if (alert.getAlertLevel() == null || alert.getAlertLevel().isBlank()) alert.setAlertLevel("INFO");
        if (alert.getCreatedAt() == null) alert.setCreatedAt(LocalDateTime.now());
        if (alert.getMessage() == null || alert.getMessage().isBlank()) {
            alert.setMessage("Low stock alert");
        }
    }

    private void requireId(Long id, String name) {
        if (id == null || id <= 0) throw new IllegalArgumentException(name + " must be positive");
    }

    private void requireNonBlank(String s, String name) {
        if (s == null || s.trim().isEmpty()) throw new IllegalArgumentException(name + " must not be blank");
    }

    private void requirePositive(int n, String name) {
        if (n <= 0) throw new IllegalArgumentException(name + " must be > 0");
    }

    private void requireNonNegative(int n, String name) {
        if (n < 0) throw new IllegalArgumentException(name + " must be >= 0");
    }
}
