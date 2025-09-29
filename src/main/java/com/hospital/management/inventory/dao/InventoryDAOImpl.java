package com.hospital.management.inventory.dao;

import com.hospital.management.inventory.model.*;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of InventoryDAO.
 * NOTE: Replace getConnection() with your ConnectionFactory / pooled DataSource and read
 * DB settings from application.properties. SQL assumes MySQL-ish schema names shown below.
 *
 * Tables assumed:
 *  - medicines(id PK, name, batch_number, expiry_date, supplier_id FK, quantity_in_stock, unit_price, status, created_at, updated_at)
 *  - equipment(id PK, name, category, model_number, serial_number, purchase_date, warranty_expiry, unit_cost, quantity_in_stock,
 *              supplier_id FK, status, created_at, updated_at)
 *  - suppliers(id PK, name, contact_person, phone, email, address, gst_number, status, created_at, updated_at)
 *  - stock_alerts(id PK, item_type, item_id, item_name, threshold, current_quantity, alert_level, status, message,
 *                 created_at, acknowledged_at, resolved_at, last_notified_at)
 *  - purchase_orders(id PK, supplier_id FK, item_type, item_id, item_name, quantity_ordered, unit_price, total_amount,
 *                    order_date, expected_delivery_date, actual_delivery_date, status, payment_status, remarks, created_at, updated_at)
 */
public class InventoryDAOImpl implements InventoryDAO {

    // -------------------- Connection --------------------
    private Connection getConnection() throws SQLException {
        // TODO: swap to ConnectionFactory and read from application.properties
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/hospital_db",
                "root",
                "password"
        );
    }

    // =====================================================================
    //                               MEDICINE
    // =====================================================================

    @Override
    public Long insertMedicine(Medicine m) {
        String sql = "INSERT INTO medicines (name, batch_number, expiry_date, supplier_id, quantity_in_stock, unit_price, status, created_at, updated_at) " +
                "VALUES (?,?,?,?,?,?,?,?,?)";
        LocalDateTime now = LocalDateTime.now();
        if (m.getCreatedAt() == null) m.setCreatedAt(now);
        if (m.getUpdatedAt() == null) m.setUpdatedAt(now);

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, m.getName());
            ps.setString(2, m.getBatchNumber());
            setDateOrNull(ps, 3, m.getExpiryDate());
            setLongOrNull(ps, 4, m.getSupplierId());
            ps.setInt(5, m.getQuantityInStock());
            setBigDecimalOrNull(ps, 6, m.getUnitPrice());
            ps.setString(7, m.getStatus());
            setTimestampOrNull(ps, 8, m.getCreatedAt());
            setTimestampOrNull(ps, 9, m.getUpdatedAt());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public Optional<Medicine> findMedicineById(Long id) {
        String sql = "SELECT * FROM medicines WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapMedicine(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    @Override
    public List<Medicine> findMedicinesByName(String name) {
        String sql = "SELECT * FROM medicines WHERE LOWER(name) LIKE ? ORDER BY name ASC";
        List<Medicine> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + (name == null ? "" : name.toLowerCase()) + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapMedicine(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public List<Medicine> findMedicinesExpiringSoon(int days) {
        String sql = "SELECT * FROM medicines WHERE expiry_date IS NOT NULL AND expiry_date <= (CURRENT_DATE + INTERVAL ? DAY) ORDER BY expiry_date ASC";
        List<Medicine> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Math.max(days, 0));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapMedicine(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public List<Medicine> findAllMedicines() {
        String sql = "SELECT * FROM medicines ORDER BY name ASC";
        List<Medicine> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapMedicine(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public boolean updateMedicine(Medicine m) {
        String sql = "UPDATE medicines SET name=?, batch_number=?, expiry_date=?, supplier_id=?, quantity_in_stock=?, unit_price=?, status=?, updated_at=? WHERE id=?";
        m.setUpdatedAt(LocalDateTime.now());
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, m.getName());
            ps.setString(2, m.getBatchNumber());
            setDateOrNull(ps, 3, m.getExpiryDate());
            setLongOrNull(ps, 4, m.getSupplierId());
            ps.setInt(5, m.getQuantityInStock());
            setBigDecimalOrNull(ps, 6, m.getUnitPrice());
            ps.setString(7, m.getStatus());
            setTimestampOrNull(ps, 8, m.getUpdatedAt());
            ps.setLong(9, m.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public boolean updateMedicineStock(Long id, int newQty) {
        String sql = "UPDATE medicines SET quantity_in_stock=?, updated_at=? WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newQty);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public boolean deleteMedicine(Long id) {
        String sql = "DELETE FROM medicines WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // =====================================================================
    //                               EQUIPMENT
    // =====================================================================

    @Override
    public Long insertEquipment(Equipment e) {
        String sql = "INSERT INTO equipment (name, category, model_number, serial_number, purchase_date, warranty_expiry, unit_cost, quantity_in_stock, supplier_id, status, created_at, updated_at) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        LocalDateTime now = LocalDateTime.now();
        if (e.getCreatedAt() == null) e.setCreatedAt(now);
        if (e.getUpdatedAt() == null) e.setUpdatedAt(now);

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, e.getName());
            ps.setString(2, e.getCategory());
            ps.setString(3, e.getModelNumber());
            ps.setString(4, e.getSerialNumber());
            setDateOrNull(ps, 5, e.getPurchaseDate());
            setDateOrNull(ps, 6, e.getWarrantyExpiry());
            setBigDecimalOrNull(ps, 7, e.getUnitCost());
            ps.setInt(8, e.getQuantityInStock());
            setLongOrNull(ps, 9, e.getSupplierId());
            ps.setString(10, e.getStatus());
            setTimestampOrNull(ps, 11, e.getCreatedAt());
            setTimestampOrNull(ps, 12, e.getUpdatedAt());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return null;
    }

    @Override
    public Optional<Equipment> findEquipmentById(Long id) {
        String sql = "SELECT * FROM equipment WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapEquipment(rs));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return Optional.empty();
    }

    @Override
    public List<Equipment> findEquipmentByCategory(String category) {
        String sql = "SELECT * FROM equipment WHERE category = ? ORDER BY name ASC";
        List<Equipment> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapEquipment(rs));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return list;
    }

    @Override
    public List<Equipment> findEquipmentUnderMaintenance() {
        String sql = "SELECT * FROM equipment WHERE status = 'UNDER_MAINTENANCE' ORDER BY updated_at DESC";
        List<Equipment> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapEquipment(rs));
        } catch (SQLException ex) { ex.printStackTrace(); }
        return list;
    }

    @Override
    public List<Equipment> findAllEquipment() {
        String sql = "SELECT * FROM equipment ORDER BY name ASC";
        List<Equipment> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapEquipment(rs));
        } catch (SQLException ex) { ex.printStackTrace(); }
        return list;
    }

    @Override
    public boolean updateEquipment(Equipment e) {
        String sql = "UPDATE equipment SET name=?, category=?, model_number=?, serial_number=?, purchase_date=?, warranty_expiry=?, unit_cost=?, quantity_in_stock=?, supplier_id=?, status=?, updated_at=? WHERE id=?";
        e.setUpdatedAt(LocalDateTime.now());
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, e.getName());
            ps.setString(2, e.getCategory());
            ps.setString(3, e.getModelNumber());
            ps.setString(4, e.getSerialNumber());
            setDateOrNull(ps, 5, e.getPurchaseDate());
            setDateOrNull(ps, 6, e.getWarrantyExpiry());
            setBigDecimalOrNull(ps, 7, e.getUnitCost());
            ps.setInt(8, e.getQuantityInStock());
            setLongOrNull(ps, 9, e.getSupplierId());
            ps.setString(10, e.getStatus());
            setTimestampOrNull(ps, 11, e.getUpdatedAt());
            ps.setLong(12, e.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); }
        return false;
    }

    @Override
    public boolean updateEquipmentStatus(Long id, String status) {
        String sql = "UPDATE equipment SET status=?, updated_at=? WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); }
        return false;
    }

    @Override
    public boolean deleteEquipment(Long id) {
        String sql = "DELETE FROM equipment WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); }
        return false;
    }

    // =====================================================================
    //                               SUPPLIER
    // =====================================================================

    @Override
    public Long insertSupplier(Supplier s) {
        String sql = "INSERT INTO suppliers (name, contact_person, phone, email, address, gst_number, status, created_at, updated_at) " +
                "VALUES (?,?,?,?,?,?,?,?,?)";
        LocalDateTime now = LocalDateTime.now();
        if (s.getCreatedAt() == null) s.setCreatedAt(now);
        if (s.getUpdatedAt() == null) s.setUpdatedAt(now);

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, s.getName());
            ps.setString(2, s.getContactPerson());
            ps.setString(3, s.getPhone());
            ps.setString(4, s.getEmail());
            ps.setString(5, s.getAddress());
            ps.setString(6, s.getGstNumber());
            ps.setString(7, s.getStatus());
            setTimestampOrNull(ps, 8, s.getCreatedAt());
            setTimestampOrNull(ps, 9, s.getUpdatedAt());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return null;
    }

    @Override
    public Optional<Supplier> findSupplierById(Long id) {
        String sql = "SELECT * FROM suppliers WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapSupplier(rs));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return Optional.empty();
    }

    @Override
    public List<Supplier> findSuppliersByName(String name) {
        String sql = "SELECT * FROM suppliers WHERE LOWER(name) LIKE ? ORDER BY name ASC";
        List<Supplier> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + (name == null ? "" : name.toLowerCase()) + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapSupplier(rs));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return list;
    }

    @Override
    public List<Supplier> findSuppliersByStatus(String status) {
        String sql = "SELECT * FROM suppliers WHERE status = ? ORDER BY name ASC";
        List<Supplier> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapSupplier(rs));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return list;
    }

    @Override
    public List<Supplier> findAllSuppliers() {
        String sql = "SELECT * FROM suppliers ORDER BY name ASC";
        List<Supplier> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapSupplier(rs));
        } catch (SQLException ex) { ex.printStackTrace(); }
        return list;
    }

    @Override
    public boolean updateSupplier(Supplier s) {
        String sql = "UPDATE suppliers SET name=?, contact_person=?, phone=?, email=?, address=?, gst_number=?, status=?, updated_at=? WHERE id=?";
        s.setUpdatedAt(LocalDateTime.now());
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, s.getName());
            ps.setString(2, s.getContactPerson());
            ps.setString(3, s.getPhone());
            ps.setString(4, s.getEmail());
            ps.setString(5, s.getAddress());
            ps.setString(6, s.getGstNumber());
            ps.setString(7, s.getStatus());
            setTimestampOrNull(ps, 8, s.getUpdatedAt());
            ps.setLong(9, s.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); }
        return false;
    }

    @Override
    public boolean deleteSupplier(Long id) {
        String sql = "DELETE FROM suppliers WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); }
        return false;
    }

    // =====================================================================
    //                               STOCK ALERT
    // =====================================================================

    @Override
    public Long insertStockAlert(StockAlert a) {
        String sql = "INSERT INTO stock_alerts (item_type, item_id, item_name, threshold, current_quantity, alert_level, status, message, created_at, acknowledged_at, resolved_at, last_notified_at) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, a.getItemType());
            setLongOrNull(ps, 2, a.getItemId());
            ps.setString(3, a.getItemName());
            ps.setInt(4, a.getThreshold());
            ps.setInt(5, a.getCurrentQuantity());
            ps.setString(6, a.getAlertLevel());
            ps.setString(7, a.getStatus());
            ps.setString(8, a.getMessage());
            setTimestampOrNull(ps, 9, a.getCreatedAt());
            setTimestampOrNull(ps, 10, a.getAcknowledgedAt());
            setTimestampOrNull(ps, 11, a.getResolvedAt());
            setTimestampOrNull(ps, 12, a.getLastNotifiedAt());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return null;
    }

    @Override
    public Optional<StockAlert> findAlertById(Long id) {
        String sql = "SELECT * FROM stock_alerts WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapStockAlert(rs));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return Optional.empty();
    }

    @Override
    public List<StockAlert> findAlertsByStatus(String status) {
        String sql = "SELECT * FROM stock_alerts WHERE status = ? ORDER BY created_at DESC";
        List<StockAlert> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapStockAlert(rs));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return list;
    }

    @Override
    public List<StockAlert> findAlertsByItem(Long itemId, String itemType) {
        String sql = "SELECT * FROM stock_alerts WHERE item_id = ? AND item_type = ? ORDER BY created_at DESC";
        List<StockAlert> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, itemId);
            ps.setString(2, itemType);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapStockAlert(rs));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return list;
    }

    @Override
    public List<StockAlert> findAllAlerts() {
        String sql = "SELECT * FROM stock_alerts ORDER BY created_at DESC";
        List<StockAlert> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapStockAlert(rs));
        } catch (SQLException ex) { ex.printStackTrace(); }
        return list;
    }

    @Override
    public boolean updateStockAlert(StockAlert a) {
        String sql = "UPDATE stock_alerts SET item_type=?, item_id=?, item_name=?, threshold=?, current_quantity=?, alert_level=?, status=?, message=?, " +
                "acknowledged_at=?, resolved_at=?, last_notified_at=? WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, a.getItemType());
            setLongOrNull(ps, 2, a.getItemId());
            ps.setString(3, a.getItemName());
            ps.setInt(4, a.getThreshold());
            ps.setInt(5, a.getCurrentQuantity());
            ps.setString(6, a.getAlertLevel());
            ps.setString(7, a.getStatus());
            ps.setString(8, a.getMessage());
            setTimestampOrNull(ps, 9, a.getAcknowledgedAt());
            setTimestampOrNull(ps, 10, a.getResolvedAt());
            setTimestampOrNull(ps, 11, a.getLastNotifiedAt());
            ps.setLong(12, a.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); }
        return false;
    }

    @Override
    public boolean closeStockAlert(Long id) {
        String sql = "UPDATE stock_alerts SET status='RESOLVED', resolved_at=? WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); }
        return false;
    }

    @Override
    public boolean deleteStockAlert(Long id) {
        String sql = "DELETE FROM stock_alerts WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); }
        return false;
    }

    // =====================================================================
    //                             PURCHASE ORDER
    // =====================================================================

    @Override
    public Long insertPurchaseOrder(PurchaseOrder po) {
        String sql = "INSERT INTO purchase_orders (supplier_id, item_type, item_id, item_name, quantity_ordered, unit_price, total_amount, " +
                "order_date, expected_delivery_date, actual_delivery_date, status, payment_status, remarks, created_at, updated_at) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        LocalDateTime now = LocalDateTime.now();
        if (po.getCreatedAt() == null) po.setCreatedAt(now);
        if (po.getUpdatedAt() == null) po.setUpdatedAt(now);

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setLongOrNull(ps, 1, po.getSupplierId());
            ps.setString(2, po.getItemType());
            setLongOrNull(ps, 3, po.getItemId());
            ps.setString(4, po.getItemName());
            ps.setInt(5, po.getQuantityOrdered());
            setBigDecimalOrNull(ps, 6, po.getUnitPrice());
            setBigDecimalOrNull(ps, 7, po.getTotalAmount());
            setDateOrNull(ps, 8, po.getOrderDate());
            setDateOrNull(ps, 9, po.getExpectedDeliveryDate());
            setDateOrNull(ps, 10, po.getActualDeliveryDate());
            ps.setString(11, po.getStatus());
            ps.setString(12, po.getPaymentStatus());
            ps.setString(13, po.getRemarks());
            setTimestampOrNull(ps, 14, po.getCreatedAt());
            setTimestampOrNull(ps, 15, po.getUpdatedAt());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return null;
    }

    @Override
    public Optional<PurchaseOrder> findPurchaseOrderById(Long id) {
        String sql = "SELECT * FROM purchase_orders WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapPurchaseOrder(rs));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return Optional.empty();
    }

    @Override
    public List<PurchaseOrder> findOrdersBySupplier(Long supplierId) {
        String sql = "SELECT * FROM purchase_orders WHERE supplier_id = ? ORDER BY order_date DESC, id DESC";
        List<PurchaseOrder> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, supplierId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapPurchaseOrder(rs));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return list;
    }

    @Override
    public List<PurchaseOrder> findOrdersByStatus(String status) {
        String sql = "SELECT * FROM purchase_orders WHERE status = ? ORDER BY order_date DESC, id DESC";
        List<PurchaseOrder> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapPurchaseOrder(rs));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return list;
    }

    @Override
    public List<PurchaseOrder> findAllPurchaseOrders() {
        String sql = "SELECT * FROM purchase_orders ORDER BY order_date DESC, id DESC";
        List<PurchaseOrder> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapPurchaseOrder(rs));
        } catch (SQLException ex) { ex.printStackTrace(); }
        return list;
    }

    @Override
    public boolean updatePurchaseOrder(PurchaseOrder po) {
        String sql = "UPDATE purchase_orders SET supplier_id=?, item_type=?, item_id=?, item_name=?, quantity_ordered=?, unit_price=?, total_amount=?, " +
                "order_date=?, expected_delivery_date=?, actual_delivery_date=?, status=?, payment_status=?, remarks=?, updated_at=? WHERE id=?";
        po.setUpdatedAt(LocalDateTime.now());
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            setLongOrNull(ps, 1, po.getSupplierId());
            ps.setString(2, po.getItemType());
            setLongOrNull(ps, 3, po.getItemId());
            ps.setString(4, po.getItemName());
            ps.setInt(5, po.getQuantityOrdered());
            setBigDecimalOrNull(ps, 6, po.getUnitPrice());
            setBigDecimalOrNull(ps, 7, po.getTotalAmount());
            setDateOrNull(ps, 8, po.getOrderDate());
            setDateOrNull(ps, 9, po.getExpectedDeliveryDate());
            setDateOrNull(ps, 10, po.getActualDeliveryDate());
            ps.setString(11, po.getStatus());
            ps.setString(12, po.getPaymentStatus());
            ps.setString(13, po.getRemarks());
            setTimestampOrNull(ps, 14, po.getUpdatedAt());
            ps.setLong(15, po.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); }
        return false;
    }

    @Override
    public boolean updatePurchaseOrderStatus(Long id, String status) {
        String sql = "UPDATE purchase_orders SET status=?, updated_at=? WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); }
        return false;
    }

    @Override
    public boolean deletePurchaseOrder(Long id) {
        String sql = "DELETE FROM purchase_orders WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); }
        return false;
    }

    // =====================================================================
    //                         Row mappers & helpers
    // =====================================================================

    private Medicine mapMedicine(ResultSet rs) throws SQLException {
        Medicine m = new Medicine();
        m.setId(rs.getLong("id"));
        m.setName(rs.getString("name"));
        m.setBatchNumber(rs.getString("batch_number"));
        m.setExpiryDate(getLocalDate(rs, "expiry_date"));
        long sid = rs.getLong("supplier_id"); m.setSupplierId(rs.wasNull() ? null : sid);
        m.setQuantityInStock(rs.getInt("quantity_in_stock"));
        m.setUnitPrice(rs.getBigDecimal("unit_price"));
        m.setStatus(rs.getString("status"));
        m.setCreatedAt(getLdt(rs, "created_at"));
        m.setUpdatedAt(getLdt(rs, "updated_at"));
        return m;
    }

    private Equipment mapEquipment(ResultSet rs) throws SQLException {
        Equipment e = new Equipment();
        e.setId(rs.getLong("id"));
        e.setName(rs.getString("name"));
        e.setCategory(rs.getString("category"));
        e.setModelNumber(rs.getString("model_number"));
        e.setSerialNumber(rs.getString("serial_number"));
        e.setPurchaseDate(getLocalDate(rs, "purchase_date"));
        e.setWarrantyExpiry(getLocalDate(rs, "warranty_expiry"));
        e.setUnitCost(rs.getBigDecimal("unit_cost"));
        e.setQuantityInStock(rs.getInt("quantity_in_stock"));
        long sid = rs.getLong("supplier_id"); e.setSupplierId(rs.wasNull() ? null : sid);
        e.setStatus(rs.getString("status"));
        e.setCreatedAt(getLdt(rs, "created_at"));
        e.setUpdatedAt(getLdt(rs, "updated_at"));
        return e;
    }

    private Supplier mapSupplier(ResultSet rs) throws SQLException {
        Supplier s = new Supplier();
        s.setId(rs.getLong("id"));
        s.setName(rs.getString("name"));
        s.setContactPerson(rs.getString("contact_person"));
        s.setPhone(rs.getString("phone"));
        s.setEmail(rs.getString("email"));
        s.setAddress(rs.getString("address"));
        s.setGstNumber(rs.getString("gst_number"));
        s.setStatus(rs.getString("status"));
        s.setCreatedAt(getLdt(rs, "created_at"));
        s.setUpdatedAt(getLdt(rs, "updated_at"));
        return s;
    }

    private StockAlert mapStockAlert(ResultSet rs) throws SQLException {
        StockAlert a = new StockAlert();
        a.setId(rs.getLong("id"));
        a.setItemType(rs.getString("item_type"));
        long itemId = rs.getLong("item_id"); a.setItemId(rs.wasNull() ? null : itemId);
        a.setItemName(rs.getString("item_name"));
        a.setThreshold(rs.getInt("threshold"));
        a.setCurrentQuantity(rs.getInt("current_quantity"));
        a.setAlertLevel(rs.getString("alert_level"));
        a.setStatus(rs.getString("status"));
        a.setMessage(rs.getString("message"));
        a.setCreatedAt(getLdt(rs, "created_at"));
        a.setAcknowledgedAt(getLdt(rs, "acknowledged_at"));
        a.setResolvedAt(getLdt(rs, "resolved_at"));
        a.setLastNotifiedAt(getLdt(rs, "last_notified_at"));
        return a;
    }

    private PurchaseOrder mapPurchaseOrder(ResultSet rs) throws SQLException {
        PurchaseOrder po = new PurchaseOrder();
        po.setId(rs.getLong("id"));
        long sid = rs.getLong("supplier_id"); po.setSupplierId(rs.wasNull() ? null : sid);
        po.setItemType(rs.getString("item_type"));
        long iid = rs.getLong("item_id"); po.setItemId(rs.wasNull() ? null : iid);
        po.setItemName(rs.getString("item_name"));
        po.setQuantityOrdered(rs.getInt("quantity_ordered"));
        po.setUnitPrice(rs.getBigDecimal("unit_price"));
        po.setTotalAmount(rs.getBigDecimal("total_amount"));
        po.setOrderDate(getLocalDate(rs, "order_date"));
        po.setExpectedDeliveryDate(getLocalDate(rs, "expected_delivery_date"));
        po.setActualDeliveryDate(getLocalDate(rs, "actual_delivery_date"));
        po.setStatus(rs.getString("status"));
        po.setPaymentStatus(rs.getString("payment_status"));
        po.setRemarks(rs.getString("remarks"));
        po.setCreatedAt(getLdt(rs, "created_at"));
        po.setUpdatedAt(getLdt(rs, "updated_at"));
        return po;
    }

    // Small setters/getters helpers

    private void setLongOrNull(PreparedStatement ps, int idx, Long value) throws SQLException {
        if (value == null) ps.setNull(idx, Types.BIGINT); else ps.setLong(idx, value);
    }

    private void setBigDecimalOrNull(PreparedStatement ps, int idx, BigDecimal value) throws SQLException {
        if (value == null) ps.setNull(idx, Types.DECIMAL); else ps.setBigDecimal(idx, value);
    }

    private void setTimestampOrNull(PreparedStatement ps, int idx, LocalDateTime ldt) throws SQLException {
        if (ldt == null) ps.setNull(idx, Types.TIMESTAMP); else ps.setTimestamp(idx, Timestamp.valueOf(ldt));
    }

    private void setDateOrNull(PreparedStatement ps, int idx, LocalDate ld) throws SQLException {
        if (ld == null) ps.setNull(idx, Types.DATE); else ps.setDate(idx, Date.valueOf(ld));
    }

    private LocalDateTime getLdt(ResultSet rs, String col) throws SQLException {
        Timestamp ts = rs.getTimestamp(col);
        return ts == null ? null : ts.toLocalDateTime();
    }

    private LocalDate getLocalDate(ResultSet rs, String col) throws SQLException {
        Date d = rs.getDate(col);
        return d == null ? null : d.toLocalDate();
    }
}