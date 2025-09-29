package com.hospital.management.billing.service;

import com.hospital.management.billing.dao.BillingDAO;
import com.hospital.management.billing.model.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * BillingServiceImpl
 * ------------------
 * Applies business rules and uses BillingDAO for persistence.
 * NOTE: If you later add a ConnectionFactory/transaction manager,
 * wrap multi-step operations (e.g., applyPayment) in a transaction.
 */
public class BillingServiceImpl implements BillingService {

    private final BillingDAO billingDAO;

    public BillingServiceImpl(BillingDAO billingDAO) {
        this.billingDAO = Objects.requireNonNull(billingDAO, "billingDAO must not be null");
    }

    // ====================================================
    //                         BILL
    // ====================================================

    @Override
    public Long createBill(Bill bill) {
        validateBillForCreate(bill);
        if (bill.getStatus() == null || bill.getStatus().isBlank()) bill.setStatus("PENDING");
        if (bill.getAmountPaid() == null) bill.setAmountPaid(BigDecimal.ZERO);
        return billingDAO.insertBill(bill);
    }

    @Override
    public Optional<Bill> getBillById(Long billId) {
        requireId(billId, "billId");
        return billingDAO.findBillById(billId);
    }

    @Override
    public List<Bill> listBills() {
        return billingDAO.findAllBills();
    }

    @Override
    public List<Bill> listBillsByPatient(Long patientId) {
        requireId(patientId, "patientId");
        return billingDAO.findBillsByPatientId(patientId);
    }

    @Override
    public Optional<Bill> getBillByAppointment(Long appointmentId) {
        requireId(appointmentId, "appointmentId");
        return billingDAO.findBillByAppointmentId(appointmentId);
    }

    @Override
    public boolean updateBill(Bill bill) {
        validateBillForUpdate(bill);
        return billingDAO.updateBill(bill);
    }

    @Override
    public boolean setBillStatus(Long billId, String status) {
        requireId(billId, "billId");
        requireNonBlank(status, "status");
        return billingDAO.updateBillStatus(billId, status);
    }

    @Override
    public boolean deleteBill(Long billId) {
        requireId(billId, "billId");
        return billingDAO.deleteBillById(billId);
    }

    @Override
    public long countBills() {
        return billingDAO.countBills();
    }

    // ====================================================
    //                       PAYMENTS
    // ====================================================

    @Override
    public Long applyPayment(Payment payment) {
        Objects.requireNonNull(payment, "payment must not be null");
        requireId(payment.getBillId(), "payment.billId");
        requirePositive(payment.getAmount(), "payment.amount");
        requireNonBlank(payment.getMethod(), "payment.method");

        // 1) Persist payment (let DAO fill id)
        if (payment.getStatus() == null || payment.getStatus().isBlank()) {
            payment.setStatus("COMPLETED"); // default to COMPLETED; adjust if you post async
        }
        Long paymentId = billingDAO.insertPayment(payment);

        // 2) Recalculate bill paid amount & status
        Optional<Bill> billOpt = billingDAO.findBillById(payment.getBillId());
        if (billOpt.isEmpty()) return paymentId; // bill deleted? nothing else to do

        Bill bill = billOpt.get();
        BigDecimal totalPaid = billingDAO.getTotalPaidForBill(bill.getId());
        bill.setAmountPaid(totalPaid);

        // Decide status
        BigDecimal total = nvl(bill.getTotalAmount());
        int cmp = totalPaid.compareTo(total);
        if (cmp >= 0 && total.signum() > 0) {
            bill.setStatus("PAID");
        } else if (totalPaid.signum() > 0) {
            bill.setStatus("PARTIAL");
        } else {
            bill.setStatus("PENDING");
        }
        billingDAO.updateBill(bill);

        // 3) Optionally sync invoice balance if exists
        billingDAO.findInvoiceByBillId(bill.getId()).ifPresent(inv -> {
            inv.setAmountPaid(totalPaid);
            inv.setBalanceDue(nvl(bill.getTotalAmount()).subtract(totalPaid).max(BigDecimal.ZERO));
            if ("PAID".equals(bill.getStatus())) inv.setStatus("PAID");
            billingDAO.updateInvoice(inv);
        });

        return paymentId;
    }

    @Override
    public List<Payment> listPaymentsForBill(Long billId) {
        requireId(billId, "billId");
        return billingDAO.findPaymentsByBillId(billId);
    }

    @Override
    public boolean setPaymentStatus(Long paymentId, String status) {
        requireId(paymentId, "paymentId");
        requireNonBlank(status, "status");
        return billingDAO.updatePaymentStatus(paymentId, status);
    }

    @Override
    public BigDecimal totalPaidForBill(Long billId) {
        requireId(billId, "billId");
        return billingDAO.getTotalPaidForBill(billId);
    }

    // ====================================================
    //                       INVOICE
    // ====================================================

    @Override
    public Long generateInvoice(Long billId, String invoiceNumber, LocalDateTime dueDate) {
        requireId(billId, "billId");

        Bill bill = billingDAO.findBillById(billId)
                .orElseThrow(() -> new IllegalArgumentException("Bill not found: " + billId));

        BigDecimal total = nvl(bill.getTotalAmount());
        BigDecimal paid = nvl(billingDAO.getTotalPaidForBill(billId));
        BigDecimal balance = total.subtract(paid).max(BigDecimal.ZERO);

        Invoice invoice = new Invoice();
        invoice.setBillId(billId);
        invoice.setPatientId(bill.getPatientId());
        invoice.setInvoiceNumber(invoiceNumber == null || invoiceNumber.isBlank()
                ? generateInvoiceNumber(billId)
                : invoiceNumber);
        invoice.setTotalAmount(total);
        invoice.setAmountPaid(paid);
        invoice.setBalanceDue(balance);
        invoice.setStatus("GENERATED");
        invoice.setIssuedDate(LocalDateTime.now());
        invoice.setDueDate(dueDate);

        // If an invoice already exists for this bill, update it instead of inserting a new one.
        Optional<Invoice> existing = billingDAO.findInvoiceByBillId(billId);
        if (existing.isPresent()) {
            Invoice toUpdate = existing.get();
            invoice.setId(toUpdate.getId());
            return billingDAO.updateInvoice(invoice) ? toUpdate.getId() : toUpdate.getId();
        } else {
            return billingDAO.insertInvoice(invoice);
        }
    }

    @Override
    public Optional<Invoice> getInvoiceById(Long invoiceId) {
        requireId(invoiceId, "invoiceId");
        return billingDAO.findInvoiceById(invoiceId);
    }

    @Override
    public Optional<Invoice> getInvoiceByBill(Long billId) {
        requireId(billId, "billId");
        return billingDAO.findInvoiceByBillId(billId);
    }

    @Override
    public boolean updateInvoice(Invoice invoice) {
        Objects.requireNonNull(invoice, "invoice must not be null");
        requireId(invoice.getId(), "invoice.id");
        return billingDAO.updateInvoice(invoice);
    }

    @Override
    public boolean setInvoiceStatus(Long invoiceId, String status) {
        requireId(invoiceId, "invoiceId");
        requireNonBlank(status, "status");
        return billingDAO.updateInvoiceStatus(invoiceId, status);
    }

    // ====================================================
    //                      INSURANCE
    // ====================================================

    @Override
    public Long submitInsuranceClaim(Insurance insurance) {
        Objects.requireNonNull(insurance, "insurance must not be null");
        requireId(insurance.getBillId(), "insurance.billId");
        requireId(insurance.getPatientId(), "insurance.patientId");
        requirePositive(nvl(insurance.getClaimAmount()), "insurance.claimAmount");

        // Cap claim to bill total or remaining balance if desired
        billingDAO.findBillById(insurance.getBillId()).ifPresent(bill -> {
            BigDecimal total = nvl(bill.getTotalAmount());
            BigDecimal paid = nvl(billingDAO.getTotalPaidForBill(bill.getId()));
            BigDecimal balance = total.subtract(paid).max(BigDecimal.ZERO);
            if (insurance.getClaimAmount() != null && insurance.getClaimAmount().compareTo(balance) > 0) {
                insurance.setClaimAmount(balance);
            }
        });

        if (insurance.getStatus() == null || insurance.getStatus().isBlank()) {
            insurance.setStatus("SUBMITTED");
        }
        insurance.setSubmittedAt(insurance.getSubmittedAt() == null ? LocalDateTime.now() : insurance.getSubmittedAt());
        return billingDAO.insertInsurance(insurance);
    }

    @Override
    public Optional<Insurance> getInsuranceById(Long insuranceId) {
        requireId(insuranceId, "insuranceId");
        return billingDAO.findInsuranceById(insuranceId);
    }

    @Override
    public List<Insurance> listInsuranceForBill(Long billId) {
        requireId(billId, "billId");
        return billingDAO.findInsuranceByBillId(billId);
    }

    @Override
    public boolean updateInsurance(Insurance insurance) {
        Objects.requireNonNull(insurance, "insurance must not be null");
        requireId(insurance.getId(), "insurance.id");
        return billingDAO.updateInsurance(insurance);
    }

    @Override
    public boolean setInsuranceStatus(Long insuranceId, String status) {
        requireId(insuranceId, "insuranceId");
        requireNonBlank(status, "status");
        return billingDAO.updateInsuranceStatus(insuranceId, status);
    }

    // ====================================================
    //                    PAYMENT PLAN
    // ====================================================

    @Override
    public Long createPaymentPlan(PaymentPlan plan) {
        Objects.requireNonNull(plan, "plan must not be null");
        requireId(plan.getBillId(), "plan.billId");
        requireId(plan.getPatientId(), "plan.patientId");
        requirePositive(nvl(plan.getTotalAmount()), "plan.totalAmount");
        if (plan.getNumberOfInstallments() <= 0)
            throw new IllegalArgumentException("numberOfInstallments must be > 0");

        if (plan.getInstallmentAmount() == null || plan.getInstallmentAmount().signum() <= 0) {
            // Equal split (rounded to cents if needed)
            BigDecimal per = plan.getTotalAmount()
                    .divide(BigDecimal.valueOf(plan.getNumberOfInstallments()), BigDecimal.ROUND_HALF_UP);
            plan.setInstallmentAmount(per);
        }
        if (plan.getStatus() == null || plan.getStatus().isBlank()) plan.setStatus("ACTIVE");
        plan.setStartDate(plan.getStartDate() == null ? LocalDateTime.now() : plan.getStartDate());
        return billingDAO.insertPaymentPlan(plan);
    }

    @Override
    public Optional<PaymentPlan> getPaymentPlanById(Long planId) {
        requireId(planId, "planId");
        return billingDAO.findPaymentPlanById(planId);
    }

    @Override
    public Optional<PaymentPlan> getActivePaymentPlanForBill(Long billId) {
        requireId(billId, "billId");
        return billingDAO.findActivePaymentPlanByBillId(billId);
    }

    @Override
    public boolean updatePaymentPlan(PaymentPlan plan) {
        Objects.requireNonNull(plan, "plan must not be null");
        requireId(plan.getId(), "plan.id");
        return billingDAO.updatePaymentPlan(plan);
    }

    @Override
    public boolean closePaymentPlan(Long planId, String finalStatus) {
        requireId(planId, "planId");
        requireNonBlank(finalStatus, "finalStatus");
        return billingDAO.closePaymentPlan(planId, finalStatus);
    }

    // ====================================================
    //                         Utils
    // ====================================================

    private void requireId(Long id, String name) {
        if (id == null || id <= 0) throw new IllegalArgumentException(name + " must be positive");
    }

    private void requireNonBlank(String s, String name) {
        if (s == null || s.trim().isEmpty()) throw new IllegalArgumentException(name + " must not be blank");
    }

    private void requirePositive(BigDecimal val, String name) {
        if (val == null || val.signum() <= 0) throw new IllegalArgumentException(name + " must be > 0");
    }

    private BigDecimal nvl(BigDecimal x) {
        return x == null ? BigDecimal.ZERO : x;
    }

    private String generateInvoiceNumber(Long billId) {
        // Example: INV-<billId>-<6chars>
        return "INV-" + billId + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    private void validateBillForCreate(Bill bill) {
        Objects.requireNonNull(bill, "bill must not be null");
        requireId(bill.getPatientId(), "bill.patientId");
        requirePositive(nvl(bill.getTotalAmount()), "bill.totalAmount");
        // appointmentId is optional
    }

    private void validateBillForUpdate(Bill bill) {
        Objects.requireNonNull(bill, "bill must not be null");
        requireId(bill.getId(), "bill.id");
        if (bill.getTotalAmount() != null && bill.getTotalAmount().signum() < 0) {
            throw new IllegalArgumentException("bill.totalAmount cannot be negative");
        }
        if (bill.getAmountPaid() != null && bill.getAmountPaid().signum() < 0) {
            throw new IllegalArgumentException("bill.amountPaid cannot be negative");
        }
    }
}