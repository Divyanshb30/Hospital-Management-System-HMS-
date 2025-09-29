package com.hospital.management.dao.impl;

import com.hospital.management.dao.interfaces.BillDAO;
import com.hospital.management.models.Bill;
import com.hospital.management.common.enums.PaymentStatus;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillDAOImpl implements BillDAO {

    @Override
    public Bill getBillById(int id) {
        String sql = "SELECT * FROM bills WHERE id = ?";
        try (Connection conn = com.hospital.management.common.config.DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToBill(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Bill> getAllBills() {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM bills";
        try (Connection conn = com.hospital.management.common.config.DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                bills.add(mapResultSetToBill(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bills;
    }

    @Override
    public boolean createBill(Bill bill) {
        String sql = "INSERT INTO bills (appointment_id, patient_id, total_amount, tax_amount, discount_amount, final_amount, status, bill_date, due_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = com.hospital.management.common.config.DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, bill.getAppointmentId());
            stmt.setLong(2, bill.getPatientId());
            stmt.setBigDecimal(3, bill.getTotalAmount());
            stmt.setBigDecimal(4, bill.getTaxAmount());
            stmt.setBigDecimal(5, bill.getDiscountAmount());
            stmt.setBigDecimal(6, bill.getFinalAmount());
            stmt.setString(7, bill.getStatus().name());
            stmt.setDate(8, Date.valueOf(bill.getBillDate()));
            stmt.setDate(9, Date.valueOf(bill.getDueDate()));

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateBill(Bill bill) {
        String sql = "UPDATE bills SET total_amount=?, tax_amount=?, discount_amount=?, final_amount=?, status=?, due_date=?, updated_at=NOW() WHERE id=?";
        try (Connection conn = com.hospital.management.common.config.DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, bill.getTotalAmount());
            stmt.setBigDecimal(2, bill.getTaxAmount());
            stmt.setBigDecimal(3, bill.getDiscountAmount());
            stmt.setBigDecimal(4, bill.getFinalAmount());
            stmt.setString(5, bill.getStatus().name());
            stmt.setDate(6, Date.valueOf(bill.getDueDate()));
            stmt.setLong(7, bill.getId());

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteBill(int id) {
        String sql = "DELETE FROM bills WHERE id = ?";
        try (Connection conn = com.hospital.management.common.config.DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Bill mapResultSetToBill(ResultSet rs) throws SQLException {
        Bill bill = new Bill();
        bill.setId(rs.getLong("id"));
        bill.setAppointmentId(rs.getLong("appointment_id"));
        bill.setPatientId(rs.getLong("patient_id"));
        bill.setTotalAmount(rs.getBigDecimal("total_amount"));
        bill.setTaxAmount(rs.getBigDecimal("tax_amount"));
        bill.setDiscountAmount(rs.getBigDecimal("discount_amount"));
        bill.setFinalAmount(rs.getBigDecimal("final_amount"));
        bill.setStatus(PaymentStatus.valueOf(rs.getString("status")));
        bill.setBillDate(rs.getDate("bill_date").toLocalDate());
        bill.setDueDate(rs.getDate("due_date").toLocalDate());
        bill.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        bill.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return bill;
    }
}
