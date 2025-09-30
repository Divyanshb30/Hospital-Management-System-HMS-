package com.hospital.management.services.impl;

import com.hospital.management.interfaces.PaymentService;
import com.hospital.management.dao.interfaces.PaymentDAO;
import com.hospital.management.dao.impl.PaymentDAOImpl;
import com.hospital.management.models.Payment;
import com.hospital.management.common.exceptions.ValidationException;

import java.util.List;
import java.util.Optional;

public class PaymentServiceImpl implements PaymentService {

    private final PaymentDAO paymentDAO = new PaymentDAOImpl();

    @Override
    public Optional<Payment> findPaymentById(Long id) {
        if (id == null) return Optional.empty();
        Payment payment = paymentDAO.getPaymentById(id.intValue());
        return Optional.ofNullable(payment);
    }

    @Override
    public List<Payment> getPaymentsByBill(Long billId) {
        if (billId == null) return List.of();

        return paymentDAO.getAllPayments().stream()
                .filter(payment -> billId.equals(payment.getBillId()))
                .toList();
    }

    @Override
    public boolean processPayment(Payment payment) {
        if (payment == null) return false;

        try {
            payment.validate();
            // Could add more process/payment gateway integration logic here
            payment.markAsProcessing();
            boolean created = paymentDAO.createPayment(payment);
            if (created) {
                payment.markAsCompleted();
                return paymentDAO.updatePayment(payment);
            }
            return false;

        } catch (ValidationException e) {
            System.err.println("Payment validation failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updatePayment(Payment payment) {
        if (payment == null || payment.getId() == null) return false;

        try {
            payment.validate();
            return paymentDAO.updatePayment(payment);
        } catch (ValidationException e) {
            System.err.println("Payment validation failed on update: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentDAO.getAllPayments();
    }
}
