package com.hospital.management.billing.dao;

import com.hospital.management.billing.model.*;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of BillingDAO.
 * NOTE: Replace getConnection() with your ConnectionFactory / pooled DataSource.
 * Assumes MySQL-like schema with tables:
 *  - bills, payments, invoices, insurance, payment_plans
 */
public class BillingDAOImpl implements BillingDAO {

    // -------------------- Connection --------------------
    private Connection getConnection() throws SQLException {
        // TODO: swap to ConnectionFactory and read from application.properties
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/hospital_db",
                "root",
                "password"
        );
    }

    // ====================================================
    //                         BILL
    // ====================================================

    @Override
    public Long insertBill(Bill bill) {
        String sql = "INSERT INTO bills (patient_id, appointment_id, total_amount, amount_paid, status, " +
                "billing_date, due_date, created_at, updated_at) VALUES (?,?,?,?,?,?,?,?,?)";
        LocalDateTime now = LocalDateTime.now();
        if (bill.getCreatedAt() == null) bill.setCreatedAt(now);
        if (bill.getUpdatedAt() == null) bill.setUpdatedAt(now);

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setLongOrNull(ps, 1, bill.getPatientId());
            setLongOrNull(ps, 2, bill.getAppointmentId());
            setBigDecimalOrNull(ps, 3, bill.getTotalAmount());
            setBigDecimalOrNull(ps, 4, bill.getAmountPaid());
            ps.setString(5, bill.getStatus());
            setTimestampOrNull(ps, 6, bill.getBillingDate());
            setTimestampOrNull(ps, 7, bill.getDueDate());
            setTimestampOrNull(ps, 8, bill.getCreatedAt());
            setTimestampOrNull(ps, 9, bill.getUpdatedAt());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // later: wrap in custom exception
        }
        return null;
    }

    @Override
    public Optional<Bill> findBillById(Long billId) {
        String sql = "SELECT * FROM bills WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, billId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapBill(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Bill> findBillsByPatientId(Long patientId) {
        String sql = "SELECT * FROM bills WHERE patient_id = ? ORDER BY billing_date DESC";
        List<Bill> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapBill(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Optional<Bill> findBillByAppointmentId(Long appointmentId) {
        String sql = "SELECT * FROM bills WHERE appointment_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, appointmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapBill(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Bill> findAllBills() {
        String sql = "SELECT * FROM bills ORDER BY billing_date DESC";
        List<Bill> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapBill(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean updateBill(Bill bill) {
        String sql = "UPDATE bills SET patient_id=?, appointment_id=?, total_amount=?, amount_paid=?, status=?," +
                " billing_date=?, due_date=?, updated_at=? WHERE id=?";
        bill.setUpdatedAt(LocalDateTime.now());
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            setLongOrNull(ps, 1, bill.getPatientId());
            setLongOrNull(ps, 2, bill.getAppointmentId());
            setBigDecimalOrNull(ps, 3, bill.getTotalAmount());
            setBigDecimalOrNull(ps, 4, bill.getAmountPaid());
            ps.setString(5, bill.getStatus());
            setTimestampOrNull(ps, 6, bill.getBillingDate());
            setTimestampOrNull(ps, 7, bill.getDueDate());
            setTimestampOrNull(ps, 8, bill.getUpdatedAt());
            ps.setLong(9, bill.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateBillStatus(Long billId, String status) {
        String sql = "UPDATE bills SET status=?, updated_at=? WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(3, billId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteBillById(Long billId) {
        String sql = "DELETE FROM bills WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, billId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public long countBills() {
        String sql = "SELECT COUNT(*) FROM bills";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    // ====================================================
    //                       PAYMENTS
    // ====================================================

    @Override
    public Long insertPayment(Payment payment) {
        String sql = "INSERT INTO payments (bill_id, amount, method, status, transaction_ref, payment_date, created_at, updated_at) " +
                "VALUES (?,?,?,?,?,?,?,?)";
        LocalDateTime now = LocalDateTime.now();
        if (payment.getCreatedAt() == null) payment.setCreatedAt(now);
        if (payment.getUpdatedAt() == null) payment.setUpdatedAt(now);

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setLongOrNull(ps, 1, payment.getBillId());
            setBigDecimalOrNull(ps, 2, payment.getAmount());
            ps.setString(3, payment.getMethod());
            ps.setString(4, payment.getStatus());
            ps.setString(5, payment.getTransactionRef());
            setTimestampOrNull(ps, 6, payment.getPaymentDate());
            setTimestampOrNull(ps, 7, payment.getCreatedAt());
            setTimestampOrNull(ps, 8, payment.getUpdatedAt());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Payment> findPaymentsByBillId(Long billId) {
        String sql = "SELECT * FROM payments WHERE bill_id = ? ORDER BY payment_date DESC, id DESC";
        List<Payment> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, billId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapPayment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean updatePaymentStatus(Long paymentId, String status) {
        String sql = "UPDATE payments SET status=?, updated_at=? WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(3, paymentId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public BigDecimal getTotalPaidForBill(Long billId) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM payments WHERE bill_id = ? AND status = 'COMPLETED'";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, billId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    // ====================================================
    //                        INVOICE
    // ====================================================

    @Override
    public Long insertInvoice(Invoice invoice) {
        String sql = "INSERT INTO invoices (bill_id, patient_id, invoice_number, total_amount, amount_paid, balance_due, status," +
                " issued_date, due_date, created_at, updated_at) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        LocalDateTime now = LocalDateTime.now();
        if (invoice.getCreatedAt() == null) invoice.setCreatedAt(now);
        if (invoice.getUpdatedAt() == null) invoice.setUpdatedAt(now);

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setLongOrNull(ps, 1, invoice.getBillId());
            setLongOrNull(ps, 2, invoice.getPatientId());
            ps.setString(3, invoice.getInvoiceNumber());
            setBigDecimalOrNull(ps, 4, invoice.getTotalAmount());
            setBigDecimalOrNull(ps, 5, invoice.getAmountPaid());
            setBigDecimalOrNull(ps, 6, invoice.getBalanceDue());
            ps.setString(7, invoice.getStatus());
            setTimestampOrNull(ps, 8, invoice.getIssuedDate());
            setTimestampOrNull(ps, 9, invoice.getDueDate());
            setTimestampOrNull(ps, 10, invoice.getCreatedAt());
            setTimestampOrNull(ps, 11, invoice.getUpdatedAt());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Optional<Invoice> findInvoiceById(Long invoiceId) {
        String sql = "SELECT * FROM invoices WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapInvoice(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Invoice> findInvoiceByBillId(Long billId) {
        String sql = "SELECT * FROM invoices WHERE bill_id = ? ORDER BY id DESC LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, billId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapInvoice(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public boolean updateInvoice(Invoice invoice) {
        String sql = "UPDATE invoices SET bill_id=?, patient_id=?, invoice_number=?, total_amount=?, amount_paid=?, balance_due=?, " +
                "status=?, issued_date=?, due_date=?, updated_at=? WHERE id=?";
        invoice.setUpdatedAt(LocalDateTime.now());
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            setLongOrNull(ps, 1, invoice.getBillId());
            setLongOrNull(ps, 2, invoice.getPatientId());
            ps.setString(3, invoice.getInvoiceNumber());
            setBigDecimalOrNull(ps, 4, invoice.getTotalAmount());
            setBigDecimalOrNull(ps, 5, invoice.getAmountPaid());
            setBigDecimalOrNull(ps, 6, invoice.getBalanceDue());
            ps.setString(7, invoice.getStatus());
            setTimestampOrNull(ps, 8, invoice.getIssuedDate());
            setTimestampOrNull(ps, 9, invoice.getDueDate());
            setTimestampOrNull(ps, 10, invoice.getUpdatedAt());
            ps.setLong(11, invoice.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateInvoiceStatus(Long invoiceId, String status) {
        String sql = "UPDATE invoices SET status=?, updated_at=? WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(3, invoiceId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ====================================================
    //                       INSURANCE
    // ====================================================

    @Override
    public Long insertInsurance(Insurance insurance) {
        String sql = "INSERT INTO insurance (patient_id, bill_id, insurance_provider, policy_number, claim_amount, approved_amount, " +
                "status, remarks, submitted_at, settled_at, created_at, updated_at) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        LocalDateTime now = LocalDateTime.now();
        if (insurance.getCreatedAt() == null) insurance.setCreatedAt(now);
        if (insurance.getUpdatedAt() == null) insurance.setUpdatedAt(now);

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setLongOrNull(ps, 1, insurance.getPatientId());
            setLongOrNull(ps, 2, insurance.getBillId());
            ps.setString(3, insurance.getInsuranceProvider());
            ps.setString(4, insurance.getPolicyNumber());
            setBigDecimalOrNull(ps, 5, insurance.getClaimAmount());
            setBigDecimalOrNull(ps, 6, insurance.getApprovedAmount());
            ps.setString(7, insurance.getStatus());
            ps.setString(8, insurance.getRemarks());
            setTimestampOrNull(ps, 9, insurance.getSubmittedAt());
            setTimestampOrNull(ps, 10, insurance.getSettledAt());
            setTimestampOrNull(ps, 11, insurance.getCreatedAt());
            setTimestampOrNull(ps, 12, insurance.getUpdatedAt());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Optional<Insurance> findInsuranceById(Long insuranceId) {
        String sql = "SELECT * FROM insurance WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, insuranceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapInsurance(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Insurance> findInsuranceByBillId(Long billId) {
        String sql = "SELECT * FROM insurance WHERE bill_id = ? ORDER BY id DESC";
        List<Insurance> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, billId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapInsurance(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean updateInsurance(Insurance insurance) {
        String sql = "UPDATE insurance SET patient_id=?, bill_id=?, insurance_provider=?, policy_number=?, claim_amount=?, " +
                "approved_amount=?, status=?, remarks=?, submitted_at=?, settled_at=?, updated_at=? WHERE id=?";
        insurance.setUpdatedAt(LocalDateTime.now());
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            setLongOrNull(ps, 1, insurance.getPatientId());
            setLongOrNull(ps, 2, insurance.getBillId());
            ps.setString(3, insurance.getInsuranceProvider());
            ps.setString(4, insurance.getPolicyNumber());
            setBigDecimalOrNull(ps, 5, insurance.getClaimAmount());
            setBigDecimalOrNull(ps, 6, insurance.getApprovedAmount());
            ps.setString(7, insurance.getStatus());
            ps.setString(8, insurance.getRemarks());
            setTimestampOrNull(ps, 9, insurance.getSubmittedAt());
            setTimestampOrNull(ps, 10, insurance.getSettledAt());
            setTimestampOrNull(ps, 11, insurance.getUpdatedAt());
            ps.setLong(12, insurance.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateInsuranceStatus(Long insuranceId, String status) {
        String sql = "UPDATE insurance SET status=?, updated_at=? WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(3, insuranceId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ====================================================
    //                     PAYMENT PLAN
    // ====================================================

    @Override
    public Long insertPaymentPlan(PaymentPlan plan) {
        String sql = "INSERT INTO payment_plans (bill_id, patient_id, total_amount, number_of_installments, " +
                "installment_amount, status, start_date, end_date, created_at, updated_at) VALUES (?,?,?,?,?,?,?,?,?,?)";
        LocalDateTime now = LocalDateTime.now();
        if (plan.getCreatedAt() == null) plan.setCreatedAt(now);
        if (plan.getUpdatedAt() == null) plan.setUpdatedAt(now);

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setLongOrNull(ps, 1, plan.getBillId());
            setLongOrNull(ps, 2, plan.getPatientId());
            setBigDecimalOrNull(ps, 3, plan.getTotalAmount());
            ps.setInt(4, plan.getNumberOfInstallments());
            setBigDecimalOrNull(ps, 5, plan.getInstallmentAmount());
            ps.setString(6, plan.getStatus());
            setTimestampOrNull(ps, 7, plan.getStartDate());
            setTimestampOrNull(ps, 8, plan.getEndDate());
            setTimestampOrNull(ps, 9, plan.getCreatedAt());
            setTimestampOrNull(ps, 10, plan.getUpdatedAt());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Optional<PaymentPlan> findPaymentPlanById(Long planId) {
        String sql = "SELECT * FROM payment_plans WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, planId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapPaymentPlan(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<PaymentPlan> findActivePaymentPlanByBillId(Long billId) {
        String sql = "SELECT * FROM payment_plans WHERE bill_id = ? AND status = 'ACTIVE' ORDER BY id DESC LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, billId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapPaymentPlan(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public boolean updatePaymentPlan(PaymentPlan plan) {
        String sql = "UPDATE payment_plans SET bill_id=?, patient_id=?, total_amount=?, number_of_installments=?, " +
                "installment_amount=?, status=?, start_date=?, end_date=?, updated_at=? WHERE id=?";
        plan.setUpdatedAt(LocalDateTime.now());

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            setLongOrNull(ps, 1, plan.getBillId());
            setLongOrNull(ps, 2, plan.getPatientId());
            setBigDecimalOrNull(ps, 3, plan.getTotalAmount());
            ps.setInt(4, plan.getNumberOfInstallments());
            setBigDecimalOrNull(ps, 5, plan.getInstallmentAmount());
            ps.setString(6, plan.getStatus());
            setTimestampOrNull(ps, 7, plan.getStartDate());
            setTimestampOrNull(ps, 8, plan.getEndDate());
            setTimestampOrNull(ps, 9, plan.getUpdatedAt());
            ps.setLong(10, plan.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean closePaymentPlan(Long planId, String finalStatus) {
        String sql = "UPDATE payment_plans SET status=?, updated_at=? WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, finalStatus);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(3, planId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ====================================================
    //                    Row Mappers & Utils
    // ====================================================

    private Bill mapBill(ResultSet rs) throws SQLException {
        Bill b = new Bill();
        b.setId(rs.getLong("id"));
        long pid = rs.getLong("patient_id");  b.setPatientId(rs.wasNull() ? null : pid);
        long aid = rs.getLong("appointment_id"); b.setAppointmentId(rs.wasNull() ? null : aid);
        b.setTotalAmount(rs.getBigDecimal("total_amount"));
        b.setAmountPaid(rs.getBigDecimal("amount_paid"));
        b.setStatus(rs.getString("status"));
        b.setBillingDate(getLdt(rs, "billing_date"));
        b.setDueDate(getLdt(rs, "due_date"));
        b.setCreatedAt(getLdt(rs, "created_at"));
        b.setUpdatedAt(getLdt(rs, "updated_at"));
        return b;
    }

    private Payment mapPayment(ResultSet rs) throws SQLException {
        Payment p = new Payment();
        p.setId(rs.getLong("id"));
        long bid = rs.getLong("bill_id"); p.setBillId(rs.wasNull() ? null : bid);
        p.setAmount(rs.getBigDecimal("amount"));
        p.setMethod(rs.getString("method"));
        p.setStatus(rs.getString("status"));
        p.setTransactionRef(rs.getString("transaction_ref"));
        p.setPaymentDate(getLdt(rs, "payment_date"));
        p.setCreatedAt(getLdt(rs, "created_at"));
        p.setUpdatedAt(getLdt(rs, "updated_at"));
        return p;
    }

    private Invoice mapInvoice(ResultSet rs) throws SQLException {
        Invoice inv = new Invoice();
        inv.setId(rs.getLong("id"));
        long bid = rs.getLong("bill_id"); inv.setBillId(rs.wasNull() ? null : bid);
        long pid = rs.getLong("patient_id"); inv.setPatientId(rs.wasNull() ? null : pid);
        inv.setInvoiceNumber(rs.getString("invoice_number"));
        inv.setTotalAmount(rs.getBigDecimal("total_amount"));
        inv.setAmountPaid(rs.getBigDecimal("amount_paid"));
        inv.setBalanceDue(rs.getBigDecimal("balance_due"));
        inv.setStatus(rs.getString("status"));
        inv.setIssuedDate(getLdt(rs, "issued_date"));
        inv.setDueDate(getLdt(rs, "due_date"));
        inv.setCreatedAt(getLdt(rs, "created_at"));
        inv.setUpdatedAt(getLdt(rs, "updated_at"));
        // NOTE: Invoice.payments not auto-populated here — fetch via findPaymentsByBillId if needed.
        return inv;
    }

    private Insurance mapInsurance(ResultSet rs) throws SQLException {
        Insurance ins = new Insurance();
        ins.setId(rs.getLong("id"));
        long pid = rs.getLong("patient_id"); ins.setPatientId(rs.wasNull() ? null : pid);
        long bid = rs.getLong("bill_id"); ins.setBillId(rs.wasNull() ? null : bid);
        ins.setInsuranceProvider(rs.getString("insurance_provider"));
        ins.setPolicyNumber(rs.getString("policy_number"));
        ins.setClaimAmount(rs.getBigDecimal("claim_amount"));
        ins.setApprovedAmount(rs.getBigDecimal("approved_amount"));
        ins.setStatus(rs.getString("status"));
        ins.setRemarks(rs.getString("remarks"));
        ins.setSubmittedAt(getLdt(rs, "submitted_at"));
        ins.setSettledAt(getLdt(rs, "settled_at"));
        ins.setCreatedAt(getLdt(rs, "created_at"));
        ins.setUpdatedAt(getLdt(rs, "updated_at"));
        return ins;
    }

    private PaymentPlan mapPaymentPlan(ResultSet rs) throws SQLException {
        PaymentPlan plan = new PaymentPlan();
        plan.setId(rs.getLong("id"));
        long bid = rs.getLong("bill_id"); plan.setBillId(rs.wasNull() ? null : bid);
        long pid = rs.getLong("patient_id"); plan.setPatientId(rs.wasNull() ? null : pid);
        plan.setTotalAmount(rs.getBigDecimal("total_amount"));
        plan.setNumberOfInstallments(rs.getInt("number_of_installments"));
        plan.setInstallmentAmount(rs.getBigDecimal("installment_amount"));
        plan.setStatus(rs.getString("status"));
        plan.setStartDate(getLdt(rs, "start_date"));
        plan.setEndDate(getLdt(rs, "end_date"));
        plan.setCreatedAt(getLdt(rs, "created_at"));
        plan.setUpdatedAt(getLdt(rs, "updated_at"));
        // NOTE: Nested installments list is not populated here (would require a separate table).
        return plan;
    }

    // --- small helpers ---

    private void setLongOrNull(PreparedStatement ps, int idx, Long value) throws SQLException {
        if (value == null) ps.setNull(idx, Types.BIGINT); else ps.setLong(idx, value);
    }

    private void setBigDecimalOrNull(PreparedStatement ps, int idx, BigDecimal value) throws SQLException {
        if (value == null) ps.setNull(idx, Types.DECIMAL); else ps.setBigDecimal(idx, value);
    }

    private void setTimestampOrNull(PreparedStatement ps, int idx, LocalDateTime ldt) throws SQLException {
        if (ldt == null) ps.setNull(idx, Types.TIMESTAMP); else ps.setTimestamp(idx, Timestamp.valueOf(ldt));
    }

    private LocalDateTime getLdt(ResultSet rs, String col) throws SQLException {
        Timestamp ts = rs.getTimestamp(col);
        return ts == null ? null : ts.toLocalDateTime();
    }
}