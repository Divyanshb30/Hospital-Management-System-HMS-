package com.hospital.management.dao.impl;

import com.hospital.management.common.config.DatabaseConfig;
import com.hospital.management.dao.interfaces.PaymentDAO;
import com.hospital.management.models.Payment;
import com.hospital.management.common.enums.PaymentStatus;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAOImpl implements PaymentDAO {

    @Override
    public Payment getPaymentById(int id) {
        String sql = "SELECT * FROM payments WHERE id = ?";
        try (Connection conn = com.hospital.management.common.config.DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToPayment(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Payment> getAllPayments() {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payments";
        try (Connection conn = com.hospital.management.common.config.DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                payments.add(mapResultSetToPayment(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payments;
    }

    @Override
    public boolean createPayment(Payment payment) {
        String sql = "INSERT INTO payments (bill_id, amount, payment_method, transaction_id, payment_date, status, notes) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = com.hospital.management.common.config.DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, payment.getId());
            stmt.setBigDecimal(2, payment.getAmount());
            stmt.setString(3, payment.getPaymentMethod().name());
            stmt.setString(4, payment.getTransactionId());
            stmt.setTimestamp(5, Timestamp.valueOf(payment.getPaymentDate()));
            stmt.setString(6, payment.getStatus().name());
            stmt.setString(7, payment.getNotes());

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updatePayment(Payment payment) {
        String sql = "UPDATE payments SET amount=?, payment_method=?, transaction_id=?, payment_date=?, status=?, notes=?, updated_at=NOW() WHERE id=?";
        try (Connection conn = com.hospital.management.common.config.DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, payment.getAmount());
            stmt.setString(2, payment.getPaymentMethod().name());
            stmt.setString(3, payment.getTransactionId());
            stmt.setTimestamp(4, Timestamp.valueOf(payment.getPaymentDate()));
            stmt.setString(5, payment.getStatus().name());
            stmt.setString(6, payment.getNotes());
            stmt.setLong(7, payment.getId());

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deletePayment(int id) {
        String sql = "DELETE FROM payments WHERE id = ?";
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

    // Add this method to PaymentDAOImpl.java
    @Override
    public List<Payment> getPaymentsByPatientId(Long patientId) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT p.* FROM payments p " +
                "INNER JOIN bills b ON p.bill_id = b.id " +
                "WHERE b.patient_id = ? ORDER BY p.payment_date DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, patientId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                payments.add(mapResultSetToPayment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payments;
    }


    private Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setId(rs.getLong("id"));
        payment.setId(rs.getLong("bill_id"));
        payment.setAmount(rs.getBigDecimal("amount"));
        payment.setPaymentMethod(com.hospital.management.common.enums.PaymentMethod.valueOf(rs.getString("payment_method")));
        payment.setTransactionId(rs.getString("transaction_id"));
        payment.setPaymentDate(rs.getTimestamp("payment_date").toLocalDateTime());
        payment.setStatus(PaymentStatus.valueOf(rs.getString("status")));
        payment.setNotes(rs.getString("notes"));
        payment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        payment.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return payment;
    }
}
