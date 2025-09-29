package com.hospital.management.inventory.dao;

import com.hospital.management.inventory.model.*;

import java.util.List;
import java.util.Optional;

/**
 * InventoryDAO
 * ------------
 * Data Access Object for hospital inventory.
 * Covers Medicines, Equipment, Suppliers, Stock Alerts, and Purchase Orders.
 */
public interface InventoryDAO {

    // -------------------- MEDICINE --------------------
    Long insertMedicine(Medicine medicine);
    Optional<Medicine> findMedicineById(Long id);
    List<Medicine> findMedicinesByName(String name);
    List<Medicine> findMedicinesExpiringSoon(int days);
    List<Medicine> findAllMedicines();
    boolean updateMedicine(Medicine medicine);
    boolean updateMedicineStock(Long id, int newQty);
    boolean deleteMedicine(Long id);

    // -------------------- EQUIPMENT --------------------
    Long insertEquipment(Equipment equipment);
    Optional<Equipment> findEquipmentById(Long id);
    List<Equipment> findEquipmentByCategory(String category);
    List<Equipment> findEquipmentUnderMaintenance();
    List<Equipment> findAllEquipment();
    boolean updateEquipment(Equipment equipment);
    boolean updateEquipmentStatus(Long id, String status);
    boolean deleteEquipment(Long id);

    // -------------------- SUPPLIER --------------------
    Long insertSupplier(Supplier supplier);
    Optional<Supplier> findSupplierById(Long id);
    List<Supplier> findSuppliersByName(String name);
    List<Supplier> findSuppliersByStatus(String status);
    List<Supplier> findAllSuppliers();
    boolean updateSupplier(Supplier supplier);
    boolean deleteSupplier(Long id);

    // -------------------- STOCK ALERT --------------------
    Long insertStockAlert(StockAlert alert);
    Optional<StockAlert> findAlertById(Long id);
    List<StockAlert> findAlertsByStatus(String status);
    List<StockAlert> findAlertsByItem(Long itemId, String itemType);
    List<StockAlert> findAllAlerts();
    boolean updateStockAlert(StockAlert alert);
    boolean closeStockAlert(Long id);
    boolean deleteStockAlert(Long id);

    // -------------------- PURCHASE ORDER --------------------
    Long insertPurchaseOrder(PurchaseOrder po);
    Optional<PurchaseOrder> findPurchaseOrderById(Long id);
    List<PurchaseOrder> findOrdersBySupplier(Long supplierId);
    List<PurchaseOrder> findOrdersByStatus(String status);
    List<PurchaseOrder> findAllPurchaseOrders();
    boolean updatePurchaseOrder(PurchaseOrder po);
    boolean updatePurchaseOrderStatus(Long id, String status);
    boolean deletePurchaseOrder(Long id);
}
