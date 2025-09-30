package com.hospital.management.interfaces;

import com.hospital.management.models.Bill;
import com.hospital.management.common.enums.PaymentStatus;
import com.hospital.management.commands.CommandResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BillingService {
    // ✅ Keep your existing methods
    Optional<Bill> findBillById(Long id);
    List<Bill> getBillsByPatient(Long patientId);
    boolean createBill(Bill bill);
    boolean updateBill(Bill bill);
    boolean markBillAsPaid(Long billId);
    List<Bill> getAllBills();

    // ✅ Add new CommandResult-based methods for appointment booking
    CommandResult generateBill(Bill bill);
    CommandResult updateBillStatus(Long billId, PaymentStatus status);
    CommandResult getBillById(Long billId);
    CommandResult getBillsByPatientId(Long patientId);
    Bill generateBill(Long appointmentId, Long patientId, BigDecimal amount,
                      BigDecimal tax, BigDecimal discount, BigDecimal total, String status);
//    void updateBillStatus(Long billId, String status);
}
