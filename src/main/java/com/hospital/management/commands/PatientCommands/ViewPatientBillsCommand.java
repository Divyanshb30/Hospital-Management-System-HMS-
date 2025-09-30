package com.hospital.management.commands.PatientCommands;

import com.hospital.management.commands.Command;
import com.hospital.management.commands.CommandResult;
import com.hospital.management.common.exceptions.DatabaseException;
import com.hospital.management.common.exceptions.ValidationException;
import com.hospital.management.common.exceptions.BusinessLogicException;
import com.hospital.management.dao.interfaces.BillDAO;
import com.hospital.management.dao.interfaces.PaymentDAO;
import com.hospital.management.dao.impl.BillDAOImpl;
import com.hospital.management.dao.impl.PaymentDAOImpl;
import com.hospital.management.models.Bill;
import com.hospital.management.models.Payment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Command to view patient's bills and payments
 */
public class ViewPatientBillsCommand implements Command {

    private final Long patientId;
    private final BillDAO billDAO;
    private final PaymentDAO paymentDAO;

    public ViewPatientBillsCommand(Long patientId) {
        this.patientId = patientId;
        this.billDAO = new BillDAOImpl();
        this.paymentDAO = new PaymentDAOImpl();
    }

    @Override
    public CommandResult execute() throws ValidationException, BusinessLogicException, DatabaseException {
        if (!validateParameters()) {
            throw new ValidationException("Invalid patient ID", "PatientId");
        }

        try {
            // Get bills for the patient
            List<Bill> bills = billDAO.getBillsByPatientId(patientId);

            // Get payments for the patient
            List<Payment> payments = paymentDAO.getPaymentsByPatientId(patientId);

            // Create result data
            Map<String, Object> billsAndPayments = new HashMap<>();
            billsAndPayments.put("bills", bills);
            billsAndPayments.put("payments", payments);
            billsAndPayments.put("patientId", patientId);

            return CommandResult.success("Bills and payments retrieved successfully", billsAndPayments);

        } catch (Exception e) {
            throw new DatabaseException("Error retrieving bills and payments: " + e.getMessage(), "BILLS_RETRIEVAL_ERROR");
        }
    }

    @Override
    public String getDescription() {
        return "View bills and payments for patient ID: " + patientId;
    }

    @Override
    public boolean validateParameters() throws ValidationException {
        return patientId != null && patientId > 0;
    }
}
