package com.hospital.management.billing.service;

import com.hospital.management.billing.model.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * BillingService
 * --------------
 * Business facade for billing workflows.
 * Applies validations and cross-entity rules, delegates persistence to BillingDAO.
 */
public interface BillingService {

    // -------------------- BILL --------------------

    /** Create a new bill and return the generated ID. */
    Long createBill(Bill bill);

    /** Fetch a bill by ID. */
    Optional<Bill> getBillById(Long billId);

    /** List all bills (consider pagination in impl). */
    List<Bill> listBills();

    /** List bills for a patient. */
    List<Bill> listBillsByPatient(Long patientId);

    /** Find bill tied to an appointment (if any). */
    Optional<Bill> getBillByAppointment(Long appointmentId);

    /** Update bill fields (amounts, due dates, etc.). */
    boolean updateBill(Bill bill);

    /** Set bill status (e.g., PENDING, PARTIAL, PAID, CANCELLED). */
    boolean setBillStatus(Long billId, String status);

    /** Delete a bill. */
    boolean deleteBill(Long billId);

    /** Count all bills. */
    long countBills();

    // -------------------- PAYMENTS --------------------

    /**
     * Apply a payment against a bill.
     * Should atomically:
     *  - insert Payment,
     *  - update Bill.amountPaid,
     *  - recalc status (PAID / PARTIAL),
     *  - optionally update related Invoice balance.
     * Returns generated payment ID.
     */
    Long applyPayment(Payment payment);

    /** List payments for a bill. */
    List<Payment> listPaymentsForBill(Long billId);

    /** Update payment status (COMPLETED, FAILED, REFUNDED). */
    boolean setPaymentStatus(Long paymentId, String status);

    /** Sum of COMPLETED payments for a bill. */
    BigDecimal totalPaidForBill(Long billId);

    // -------------------- INVOICE --------------------

    /**
     * Generate (or regenerate) an invoice for a bill.
     * Should compute amounts (total/paid/balance) and assign invoiceNumber if missing.
     */
    Long generateInvoice(Long billId, String invoiceNumber, LocalDateTime dueDate);

    /** Fetch invoice by ID. */
    Optional<Invoice> getInvoiceById(Long invoiceId);

    /** Fetch invoice by bill ID (often 1:1). */
    Optional<Invoice> getInvoiceByBill(Long billId);

    /** Update invoice details. */
    boolean updateInvoice(Invoice invoice);

    /** Set invoice status (GENERATED, SENT, PAID, CANCELLED). */
    boolean setInvoiceStatus(Long invoiceId, String status);

    // -------------------- INSURANCE --------------------

    /**
     * Submit an insurance claim.
     * May validate policy, cap claimAmount to bill/balance, and mark bill as PENDING_INSURANCE (optional).
     */
    Long submitInsuranceClaim(Insurance insurance);

    /** Get insurance claim by ID. */
    Optional<Insurance> getInsuranceById(Long insuranceId);

    /** List insurance claims for a bill. */
    List<Insurance> listInsuranceForBill(Long billId);

    /** Update an insurance record (amounts, remarks, timestamps). */
    boolean updateInsurance(Insurance insurance);

    /** Move insurance through workflow (SUBMITTED, APPROVED, REJECTED, SETTLED). */
    boolean setInsuranceStatus(Long insuranceId, String status);

    // -------------------- PAYMENT PLAN --------------------

    /**
     * Create a payment plan for a bill.
     * Should validate totals, compute installment schedule when needed.
     */
    Long createPaymentPlan(PaymentPlan plan);

    /** Get payment plan by ID. */
    Optional<PaymentPlan> getPaymentPlanById(Long planId);

    /** Get active payment plan for a bill, if any. */
    Optional<PaymentPlan> getActivePaymentPlanForBill(Long billId);

    /** Update plan details (installments, dates, status). */
    boolean updatePaymentPlan(PaymentPlan plan);

    /** Close a plan with final status (COMPLETED, DEFAULTED, CANCELLED). */
    boolean closePaymentPlan(Long planId, String finalStatus);
}