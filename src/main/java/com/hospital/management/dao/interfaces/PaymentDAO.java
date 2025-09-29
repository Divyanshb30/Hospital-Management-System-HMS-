package com.hospital.management.dao.interfaces;


import com.hospital.management.models.Payment;
import java.util.List;

public interface PaymentDAO {
    Payment getPaymentById(int id);
    List<Payment> getAllPayments();
    boolean createPayment(Payment payment);
    boolean updatePayment(Payment payment);
    boolean deletePayment(int id);
}
