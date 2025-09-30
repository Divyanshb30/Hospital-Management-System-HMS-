package com.hospital.management.services.impl;

import com.hospital.management.interfaces.BillingService;
import com.hospital.management.dao.interfaces.BillDAO;
import com.hospital.management.dao.impl.BillDAOImpl;
import com.hospital.management.models.Bill;
import com.hospital.management.common.enums.PaymentStatus;
import com.hospital.management.common.exceptions.ValidationException;

import java.util.List;
import java.util.Optional;

public class BillingServiceImpl implements BillingService {

    private final BillDAO billDAO = new BillDAOImpl();

    @Override
    public Optional<Bill> findBillById(Long id) {
        if (id == null) return Optional.empty();
        Bill bill = billDAO.getBillById(id.intValue());
        return Optional.ofNullable(bill);
    }

    @Override
    public List<Bill> getBillsByPatient(Long patientId) {
        if (patientId == null) return List.of();

        return billDAO.getAllBills().stream()
                .filter(bill -> patientId.equals(bill.getPatientId()))
                .toList();
    }

    @Override
    public boolean createBill(Bill bill) {
        if (bill == null) return false;

        try {
            bill.validate();
            return billDAO.createBill(bill);
        } catch (ValidationException e) {
            System.err.println("Bill validation failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateBill(Bill bill) {
        if (bill == null || bill.getId() == null) return false;

        try {
            bill.validate();
            return billDAO.updateBill(bill);
        } catch (ValidationException e) {
            System.err.println("Bill validation failed on update: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean markBillAsPaid(Long billId) {
        if (billId == null) return false;

        Optional<Bill> billOpt = findBillById(billId);
        if (billOpt.isEmpty()) return false;

        Bill bill = billOpt.get();
        if (bill.isPaid()) return true; // Already paid

        bill.markAsPaid();
        return billDAO.updateBill(bill);
    }

    @Override
    public List<Bill> getAllBills() {
        return billDAO.getAllBills();
    }
}
