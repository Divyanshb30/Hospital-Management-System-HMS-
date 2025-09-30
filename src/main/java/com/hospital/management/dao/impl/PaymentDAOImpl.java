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
        try (Connection conn = DatabaseConfig.getConnection();
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

        try (Connection conn = DatabaseConfig.getConnection();
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
        // FIXED: Use proper INSERT with auto-generated key retrieval
        String sql = "INSERT INTO payments (bill_id, amount, payment_method, transaction_id, payment_date, status, notes, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // FIXED: Use getBillId() instead of getId()
            stmt.setLong(1, payment.getBillId());
            stmt.setBigDecimal(2, payment.getAmount());
            stmt.setString(3, payment.getPaymentMethod().name());
            stmt.setString(4, payment.getTransactionId());
            stmt.setTimestamp(5, Timestamp.valueOf(payment.getPaymentDate()));
            stmt.setString(6, payment.getStatus().name());
            stmt.setString(7, payment.getNotes());

            int rows = stmt.executeUpdate();

            // CRITICAL FIX: Retrieve and set the auto-generated ID
            if (rows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        payment.setId(generatedKeys.getLong(1));
                        System.out.println("✅ Payment created with ID: " + payment.getId());
                        return true;
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updatePayment(Payment payment) {
        // FIXED: Check if payment has valid ID before update
        if (payment.getId() == null) {
            System.err.println("❌ Cannot update payment: ID is null");
            return false;
        }

        String sql = "UPDATE payments SET amount=?, payment_method=?, transaction_id=?, payment_date=?, status=?, notes=?, updated_at=NOW() WHERE id=?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, payment.getAmount());
            stmt.setString(2, payment.getPaymentMethod().name());
            stmt.setString(3, payment.getTransactionId());
            stmt.setTimestamp(4, Timestamp.valueOf(payment.getPaymentDate()));
            stmt.setString(5, payment.getStatus().name());
            stmt.setString(6, payment.getNotes());
            stmt.setLong(7, payment.getId()); // Now safe to use

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

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

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
        // FIXED: Properly set billId from bill_id column
        payment.setBillId(rs.getLong("bill_id"));
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
