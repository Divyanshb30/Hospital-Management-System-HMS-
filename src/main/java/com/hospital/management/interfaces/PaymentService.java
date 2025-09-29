package com.hospital.management.interfaces;


import com.hospital.management.models.Payment;
import java.util.List;
import java.util.Optional;

public interface PaymentService {
    Optional<Payment> findPaymentById(Long id);
    List<Payment> getPaymentsByBill(Long billId);
    boolean processPayment(Payment payment);
    boolean updatePayment(Payment payment);
    List<Payment> getAllPayments();
}
