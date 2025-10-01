package com.hospital.management.commands.PatientCommands;

import com.hospital.management.commands.Command;
import com.hospital.management.commands.CommandResult;
import com.hospital.management.common.exceptions.ValidationException;
import com.hospital.management.common.exceptions.DatabaseException;
import com.hospital.management.interfaces.*;
import com.hospital.management.models.*;
import com.hospital.management.common.enums.AppointmentStatus;
import com.hospital.management.common.enums.PaymentStatus;
import com.hospital.management.common.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

public class BookAppointmentCommand implements Command {
    private final Long patientId;
    private final Long doctorId;
    private final LocalDate appointmentDate;
    private final LocalTime appointmentTime;
    private final String reason;
    private final PaymentMethod paymentMethod;

    private final AppointmentService appointmentService;
    private final DoctorService doctorService;
    private final BillingService billingService;
    private final PaymentService paymentService;

    public BookAppointmentCommand(Long patientId, Long doctorId, LocalDate appointmentDate,
                                  LocalTime appointmentTime, String reason, PaymentMethod paymentMethod,
                                  AppointmentService appointmentService, DoctorService doctorService,
                                  BillingService billingService, PaymentService paymentService) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.reason = reason;
        this.paymentMethod = paymentMethod;
        this.appointmentService = appointmentService;
        this.doctorService = doctorService;
        this.billingService = billingService;
        this.paymentService = paymentService;
    }

    @Override
    public CommandResult execute() throws ValidationException, DatabaseException {
        try {
            // Step 1: Get doctor details for consultation fee
            Doctor doctor = null;
            try {
                doctor = doctorService.getDoctorById(doctorId);
            } catch (Exception e) {
                return CommandResult.failure("Doctor not found with ID: " + doctorId, null);
            }

            if (doctor == null) {
                return CommandResult.failure("Doctor not found with ID: " + doctorId, null);
            }

            // Step 2: Create appointment FIRST
            CommandResult appointmentResult = appointmentService.bookAppointment(
                    patientId, doctorId, appointmentDate, appointmentTime, reason);

            if (!appointmentResult.isSuccess()) {
                return appointmentResult;
            }

            Appointment bookedAppointment = (Appointment) appointmentResult.getData();

            if (bookedAppointment == null || bookedAppointment.getId() == null) {
                return CommandResult.failure("Failed to create appointment - no ID generated", null);
            }

            System.out.println("✅ DEBUG: Appointment created with ID: " + bookedAppointment.getId());

            // Step 3: Generate bill AFTER appointment is created
            Bill bill = new Bill();
            bill.setAppointmentId(bookedAppointment.getId());
            bill.setPatientId(patientId);
            bill.setTotalAmount(doctor.getConsultationFee());
            bill.setTaxAmount(doctor.getConsultationFee().multiply(BigDecimal.valueOf(0.18)));
            bill.setDiscountAmount(BigDecimal.ZERO);
            bill.setFinalAmount(bill.getTotalAmount().add(bill.getTaxAmount()));
            bill.setStatus(PaymentStatus.PENDING);

            System.out.println("✅ DEBUG: Creating bill with Appointment ID: " + bill.getAppointmentId());

            // Step 4: Create bill
            boolean billCreated = billingService.createBill(bill);
            if (!billCreated) {
                return CommandResult.failure("Failed to generate bill", null);
            }

            // ✅ CRITICAL FIX: Get the created bill back from database with its ID
            List<Bill> recentBills = billingService.getAllBills().stream()
                    .filter(b -> bookedAppointment.getId().equals(b.getAppointmentId()) &&
                            patientId.equals(b.getPatientId()))
                    .collect(Collectors.toList());

            Bill createdBill = null;
            if (!recentBills.isEmpty()) {
                createdBill = recentBills.get(recentBills.size() - 1); // Get the latest bill
                System.out.println("✅ DEBUG: Bill retrieved with ID: " + createdBill.getId());
            } else {
                return CommandResult.failure("Bill created but could not retrieve ID", null);
            }

            if (createdBill.getId() == null) {
                return CommandResult.failure("Bill created but ID is null", null);
            }

            System.out.println("✅ DEBUG: Bill created successfully with ID: " + createdBill.getId());

            // Step 5: Process payment using the retrieved bill with ID
            Payment payment = new Payment();
            payment.setBillId(createdBill.getId()); // ✅ Now we have the bill ID
            payment.setAmount(createdBill.getFinalAmount());
            payment.setPaymentMethod(paymentMethod);
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setTransactionId("TXN" + System.currentTimeMillis());

            System.out.println("✅ DEBUG: Creating payment for Bill ID: " + createdBill.getId());

            // Process payment
            CommandResult paymentResult = paymentService.processPayment(payment);
            if (!paymentResult.isSuccess()) {
                return CommandResult.failure("Payment failed: " + paymentResult.getMessage(), null);
            }

            System.out.println("✅ DEBUG: Payment processed successfully");

            // Step 6: Mark bill as paid using the retrieved bill
            createdBill.setStatus(PaymentStatus.COMPLETED);
            billingService.markBillAsPaid(createdBill.getId());

            // Step 7: Update appointment status (if method exists)
            try {
                appointmentService.updateAppointmentStatus(bookedAppointment.getId(), AppointmentStatus.SCHEDULED);
            } catch (Exception e) {
                System.out.println("Warning: Could not update appointment status: " + e.getMessage());
            }

            // Prepare result data using the retrieved bill with proper ID
            Map<String, Object> resultData = new HashMap<>();
            resultData.put("appointment", bookedAppointment);
            resultData.put("bill", createdBill); // Use the bill with ID
            resultData.put("payment", paymentResult.getData() != null ? paymentResult.getData() : payment);
            resultData.put("doctor", doctor);

            System.out.println("✅ DEBUG: Appointment booking completed successfully");
            return CommandResult.success("Appointment booked and payment completed successfully", resultData);

        } catch (Exception e) {
            System.err.println("❌ ERROR in BookAppointmentCommand: " + e.getMessage());
            e.printStackTrace();
            throw new DatabaseException("Error booking appointment: " + e.getMessage(), "APPOINTMENT_BOOKING_ERROR");
        }
    }

    @Override
    public String getDescription() {
        return "Book appointment with payment processing";
    }

    @Override
    public boolean validateParameters() throws ValidationException {
        if (patientId == null || patientId <= 0) {
            throw new ValidationException("Valid patient ID is required", "PatientId");
        }
        if (doctorId == null || doctorId <= 0) {
            throw new ValidationException("Valid doctor ID is required", "DoctorId");
        }
        if (appointmentDate == null || appointmentDate.isBefore(LocalDate.now())) {
            throw new ValidationException("Valid future appointment date is required", "AppointmentDate");
        }
        if (appointmentTime == null) {
            throw new ValidationException("Appointment time is required", "AppointmentTime");
        }
        if (paymentMethod == null) {
            throw new ValidationException("Payment method is required", "PaymentMethod");
        }
        return true;
    }
}
