package com.hospital.management.billing.dao;

import com.hospital.management.billing.model.Bill;
import com.hospital.management.billing.model.Payment;
import com.hospital.management.billing.model.Invoice;
import com.hospital.management.billing.model.Insurance;
import com.hospital.management.billing.model.PaymentPlan;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * BillingDAO
 * ----------
 * DAO contract for core billing workflows.
 * Focuses on Bill as the root aggregate, with convenience access
 * to related Payments, Invoice, Insurance, and PaymentPlan.
 *
 * (You can later split this into separate DAOs per entity if desired.)
 */
public interface BillingDAO {

    // -------------------- BILL --------------------

    /** Insert a new bill and return the generated ID. */
    Long insertBill(Bill bill);

    /** Find bill by primary key. */
    Optional<Bill> findBillById(Long billId);

    /** Find bills for a specific patient. */
    List<Bill> findBillsByPatientId(Long patientId);

    /** Optionally link bills to appointments. */
    Optional<Bill> findBillByAppointmentId(Long appointmentId);

    /** List all bills (consider pagination in impl). */
    List<Bill> findAllBills();

    /** Update an existing bill (amounts, dates, etc.). */
    boolean updateBill(Bill bill);

    /** Update only bill status (PENDING, PARTIAL, PAID, CANCELLED). */
    boolean updateBillStatus(Long billId, String status);

    /** Delete bill by ID. */
    boolean deleteBillById(Long billId);

    /** Count all bills. */
    long countBills();

    // -------------------- PAYMENTS --------------------

    /** Insert a payment for a bill and return the generated payment ID. */
    Long insertPayment(Payment payment);

    /** List all payments made against a bill. */
    List<Payment> findPaymentsByBillId(Long billId);

    /** Update payment status (PENDING, COMPLETED, FAILED, REFUNDED). */
    boolean updatePaymentStatus(Long paymentId, String status);

    /** Sum of all COMPLETED payments for a bill. */
    BigDecimal getTotalPaidForBill(Long billId);

    // -------------------- INVOICE --------------------

    /** Create an invoice and return generated ID. */
    Long insertInvoice(Invoice invoice);

    /** Fetch invoice by ID. */
    Optional<Invoice> findInvoiceById(Long invoiceId);

    /** Fetch invoice by bill ID (most systems keep 1:1). */
    Optional<Invoice> findInvoiceByBillId(Long billId);

    /** Update invoice fields (amounts, number, dates). */
    boolean updateInvoice(Invoice invoice);

    /** Update invoice status (GENERATED, SENT, PAID, CANCELLED). */
    boolean updateInvoiceStatus(Long invoiceId, String status);

    // -------------------- INSURANCE --------------------

    /** Insert an insurance claim and return generated ID. */
    Long insertInsurance(Insurance insurance);

    /** Fetch insurance claim by ID. */
    Optional<Insurance> findInsuranceById(Long insuranceId);

    /** List insurance claims tied to a bill. */
    List<Insurance> findInsuranceByBillId(Long billId);

    /** Update insurance claim fields (amounts, remarks, timestamps). */
    boolean updateInsurance(Insurance insurance);

    /** Update insurance status (SUBMITTED, APPROVED, REJECTED, PENDING, SETTLED). */
    boolean updateInsuranceStatus(Long insuranceId, String status);

    // -------------------- PAYMENT PLAN --------------------

    /** Create a payment plan and return generated ID. */
    Long insertPaymentPlan(PaymentPlan plan);

    /** Fetch payment plan by ID. */
    Optional<PaymentPlan> findPaymentPlanById(Long planId);

    /** Get the active payment plan for a bill, if any. */
    Optional<PaymentPlan> findActivePaymentPlanByBillId(Long billId);

    /** Update a payment plan (e.g., installments, dates, status). */
    boolean updatePaymentPlan(PaymentPlan plan);

    /** Close a payment plan with final status (COMPLETED, DEFAULTED, CANCELLED). */
    boolean closePaymentPlan(Long planId, String finalStatus);
}
