package com.hospital.management.services.impl;

import com.hospital.management.interfaces.PaymentService;
import com.hospital.management.dao.interfaces.PaymentDAO;
import com.hospital.management.dao.impl.PaymentDAOImpl;
import com.hospital.management.models.Payment;
import com.hospital.management.commands.CommandResult;
import com.hospital.management.common.exceptions.ValidationException;
import java.util.List;
import java.util.Optional;

public class PaymentServiceImpl implements PaymentService {

    private final PaymentDAO paymentDAO = new PaymentDAOImpl();

    @Override
    public Optional<Payment> findPaymentById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        Payment payment = paymentDAO.getPaymentById(id.intValue());
        return Optional.ofNullable(payment);
    }

    @Override
    public List<Payment> getPaymentsByBill(Long billId) {
        if (billId == null) {
            return List.of();
        }
        return paymentDAO.getAllPayments().stream()
                .filter(payment -> billId.equals(payment.getBillId()))
                .toList();
    }

    @Override
    public CommandResult processPayment(Payment payment) {
        try {
            payment.validate();
            boolean created = paymentDAO.createPayment(payment);

            if (created && payment.getId() != null) {
                System.out.println("✅ Payment processed successfully with ID: " + payment.getId());

                if (payment.requiresProcessing()) {
                    payment.markAsCompleted();
                    boolean updated = paymentDAO.updatePayment(payment);
                    if (updated) {
                        System.out.println("✅ Payment status updated to: " + payment.getStatus());
                    }
                }

                return CommandResult.success("Payment processed successfully", payment);
            } else {
                return CommandResult.failure("Failed to create payment record");
            }

        } catch (ValidationException e) {
            return CommandResult.failure("Payment validation failed: " + e.getMessage());
        } catch (Exception e) {
            return CommandResult.failure("Payment processing error: " + e.getMessage());
        }
    }

    @Override
    public boolean updatePayment(Payment payment) {
        try {
            if (payment.getId() == null) {
                System.err.println("❌ Cannot update payment: Payment ID is null");
                return false;
            }

            payment.validate();
            boolean updated = paymentDAO.updatePayment(payment);
            if (updated) {
                System.out.println("✅ Payment updated successfully");
                return true;
            } else {
                System.err.println("❌ Failed to update payment");
                return false;
            }

        } catch (ValidationException e) {
            System.err.println("❌ Payment validation failed: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error updating payment: " + e.getMessage());
            return false;
        }
    }

    // ADD THIS MISSING METHOD
    @Override
    public List<Payment> getAllPayments() {
        try {
            return paymentDAO.getAllPayments();
        } catch (Exception e) {
            System.err.println("❌ Error getting all payments: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<Payment> getPaymentsByPatient(Long patientId) {
        if (patientId == null) {
            return List.of();
        }
        return paymentDAO.getPaymentsByPatientId(patientId);
    }
}
