package com.hospital.management.interfaces;


import com.hospital.management.models.Bill;
import java.util.List;
import java.util.Optional;

public interface BillingService {
    Optional<Bill> findBillById(Long id);
    List<Bill> getBillsByPatient(Long patientId);
    boolean createBill(Bill bill);
    boolean updateBill(Bill bill);
    boolean markBillAsPaid(Long billId);
    List<Bill> getAllBills();
}

