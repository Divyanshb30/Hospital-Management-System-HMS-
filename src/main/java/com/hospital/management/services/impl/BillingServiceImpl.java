package com.hospital.management.services.impl;

import com.hospital.management.interfaces.BillingService;
import com.hospital.management.models.Bill;
import com.hospital.management.common.enums.PaymentStatus;
import com.hospital.management.commands.CommandResult;
import com.hospital.management.dao.interfaces.BillDAO;
import com.hospital.management.dao.impl.BillDAOImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class BillingServiceImpl implements BillingService {
    private final BillDAO billDAO = new BillDAOImpl();

    // ✅ EXISTING METHODS (keep as they are):
    @Override
    public Optional<Bill> findBillById(Long id) {
        if (id == null) return Optional.empty();
        Bill bill = billDAO.getBillById(id.intValue());
        return Optional.ofNullable(bill);
    }

    @Override
    public List<Bill> getBillsByPatient(Long patientId) {
        return billDAO.getBillsByPatientId(patientId);
    }

    @Override
    public boolean createBill(Bill bill) {
        try {
            bill.validate();
            return billDAO.createBill(bill);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateBill(Bill bill) {
        try {
            bill.validate();
            return billDAO.updateBill(bill);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean markBillAsPaid(Long billId) {
        try {
            Bill bill = billDAO.getBillById(billId.intValue());
            if (bill != null) {
                bill.setStatus(PaymentStatus.COMPLETED);  // ✅ Use PaymentStatus
                return billDAO.updateBill(bill);
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Bill> getAllBills() {
        return billDAO.getAllBills();
    }

    // ✅ NEW COMMANDRESULT-BASED METHODS (for appointment booking):
    @Override
    public CommandResult generateBill(Bill bill) {
        try {
            bill.validate();
            boolean created = billDAO.createBill(bill);
            if (created) {
                return CommandResult.success("Bill generated successfully", bill);
            } else {
                return CommandResult.failure("Failed to generate bill", null);
            }
        } catch (Exception e) {
            return CommandResult.failure("Error generating bill: " + e.getMessage(), null);
        }
    }

    @Override
    public CommandResult updateBillStatus(Long billId, PaymentStatus status) {
        try {
            Bill bill = billDAO.getBillById(billId.intValue());
            if (bill != null) {
                bill.setStatus(status);
                boolean updated = billDAO.updateBill(bill);
                if (updated) {
                    return CommandResult.success("Bill status updated successfully", bill);
                } else {
                    return CommandResult.failure("Failed to update bill status", null);
                }
            } else {
                return CommandResult.failure("Bill not found", null);
            }
        } catch (Exception e) {
            return CommandResult.failure("Error updating bill status: " + e.getMessage(), null);
        }
    }

    @Override
    public CommandResult getBillById(Long billId) {
        try {
            Bill bill = billDAO.getBillById(billId.intValue());
            if (bill != null) {
                return CommandResult.success("Bill retrieved successfully", bill);
            } else {
                return CommandResult.failure("Bill not found", null);
            }
        } catch (Exception e) {
            return CommandResult.failure("Error retrieving bill: " + e.getMessage(), null);
        }
    }

    @Override
    public CommandResult getBillsByPatientId(Long patientId) {
        try {
            List<Bill> bills = billDAO.getBillsByPatientId(patientId);
            return CommandResult.success("Bills retrieved successfully", bills);
        } catch (Exception e) {
            return CommandResult.failure("Error retrieving bills: " + e.getMessage(), null);
        }
    }

    @Override
    public Bill generateBill(Long appointmentId, Long patientId, BigDecimal amount, BigDecimal tax, BigDecimal discount, BigDecimal total, String status) {
        return null;
    }
}
