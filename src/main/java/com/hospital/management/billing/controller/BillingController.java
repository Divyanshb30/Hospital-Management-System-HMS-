package com.hospital.management.billing.controller;

import com.hospital.management.billing.model.*;
import com.hospital.management.billing.service.BillingService;
import com.hospital.management.billing.service.PaymentProcessor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * BillingController
 * -----------------
 * CLI menu for Billing: bills, payments, invoices, insurance, and payment plans.
 * Wire this into your main app alongside Patient/Appointment/Department menus.
 */
public class BillingController {

    private final BillingService billingService;
    private final PaymentProcessor paymentProcessor; // optional helper
    private final Scanner scanner;

    public BillingController(BillingService billingService, PaymentProcessor paymentProcessor) {
        this.billingService = billingService;
        this.paymentProcessor = paymentProcessor;
        this.scanner = new Scanner(System.in);
    }

    public BillingController(BillingService billingService) {
        this(billingService, null);
    }

    /** Start menu loop */
    public void startMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== Billing Management ===");
            System.out.println("1. Create Bill");
            System.out.println("2. List Bills");
            System.out.println("3. View Bill by ID");
            System.out.println("4. Apply Payment");
            System.out.println("5. Generate/Update Invoice");
            System.out.println("6. View Invoice by Bill ID");
            System.out.println("7. Submit Insurance Claim");
            System.out.println("8. Create Payment Plan");
            System.out.println("9. Delete Bill");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1": createBill(); break;
                case "2": listBills(); break;
                case "3": viewBill(); break;
                case "4": applyPayment(); break;
                case "5": generateInvoice(); break;
                case "6": viewInvoiceByBill(); break;
                case "7": submitInsurance(); break;
                case "8": createPaymentPlan(); break;
                case "9": deleteBill(); break;
                case "0": running = false; break;
                default: System.out.println("❌ Invalid choice, try again.");
            }
        }
    }

    // -------------------- Actions --------------------

    private void createBill() {
        try {
            System.out.print("Patient ID: ");
            Long patientId = Long.parseLong(scanner.nextLine());

            System.out.print("Appointment ID (blank if none): ");
            String apptIn = scanner.nextLine();
            Long apptId = apptIn.isBlank() ? null : Long.parseLong(apptIn);

            System.out.print("Total Amount: ");
            BigDecimal total = new BigDecimal(scanner.nextLine());

            System.out.print("Due days from now (blank = none): ");
            String dueIn = scanner.nextLine();
            LocalDateTime dueDate = dueIn.isBlank() ? null : LocalDateTime.now().plusDays(Long.parseLong(dueIn));

            Bill bill = new Bill(null, patientId, apptId, total, BigDecimal.ZERO, "PENDING",
                    LocalDateTime.now(), dueDate, LocalDateTime.now(), LocalDateTime.now());

            Long id = billingService.createBill(bill);
            System.out.println("✅ Bill created with ID: " + id);
        } catch (Exception e) {
            System.out.println("❌ Error creating bill: " + e.getMessage());
        }
    }

    private void listBills() {
        List<Bill> list = billingService.listBills();
        if (list.isEmpty()) System.out.println("No bills found.");
        else list.forEach(System.out::println);
    }

    private void viewBill() {
        try {
            System.out.print("Bill ID: ");
            Long id = Long.parseLong(scanner.nextLine());
            Optional<Bill> bill = billingService.getBillById(id);
            bill.ifPresentOrElse(System.out::println, () -> System.out.println("Not found."));
        } catch (Exception e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private void applyPayment() {
        try {
            System.out.print("Bill ID: ");
            Long billId = Long.parseLong(scanner.nextLine());

            System.out.print("Amount: ");
            BigDecimal amount = new BigDecimal(scanner.nextLine());

            System.out.print("Method (CASH/CARD/UPI/INSURANCE): ");
            String method = scanner.nextLine();

            System.out.print("Transaction Ref (optional): ");
            String ref = scanner.nextLine();

            Payment p = new Payment(null, billId, amount, method, "COMPLETED",
                    ref.isBlank() ? null : ref, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());

            Long paymentId;
            if (paymentProcessor != null) {
                paymentId = paymentProcessor.processPayment(p);
            } else {
                // Use service if you prefer all business logic centralized there
                paymentId = billingService.applyPayment(p);
            }
            System.out.println("✅ Payment recorded. ID: " + paymentId);

            // Show updated bill summary
            billingService.getBillById(billId).ifPresent(b -> {
                System.out.println("Updated Bill: " + b);
            });

        } catch (Exception e) {
            System.out.println("❌ Error applying payment: " + e.getMessage());
        }
    }

    private void generateInvoice() {
        try {
            System.out.print("Bill ID: ");
            Long billId = Long.parseLong(scanner.nextLine());

            System.out.print("Invoice Number (blank to auto-generate): ");
            String num = scanner.nextLine();

            System.out.print("Due in days (blank = none): ");
            String dueIn = scanner.nextLine();
            LocalDateTime due = dueIn.isBlank() ? null : LocalDateTime.now().plusDays(Long.parseLong(dueIn));

            Long invoiceId = billingService.generateInvoice(billId, num, due);
            System.out.println("✅ Invoice ready. ID: " + invoiceId);

            billingService.getInvoiceByBill(billId).ifPresent(inv ->
                    System.out.println("Invoice: " + inv)
            );
        } catch (Exception e) {
            System.out.println("❌ Error generating invoice: " + e.getMessage());
        }
    }

    private void viewInvoiceByBill() {
        try {
            System.out.print("Bill ID: ");
            Long billId = Long.parseLong(scanner.nextLine());
            Optional<Invoice> inv = billingService.getInvoiceByBill(billId);
            inv.ifPresentOrElse(System.out::println, () -> System.out.println("No invoice for this bill."));
        } catch (Exception e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private void submitInsurance() {
        try {
            System.out.print("Bill ID: ");
            Long billId = Long.parseLong(scanner.nextLine());

            System.out.print("Patient ID: ");
            Long patientId = Long.parseLong(scanner.nextLine());

            System.out.print("Provider: ");
            String provider = scanner.nextLine();

            System.out.print("Policy Number: ");
            String policy = scanner.nextLine();

            System.out.print("Claim Amount: ");
            BigDecimal claim = new BigDecimal(scanner.nextLine());

            Insurance ins = new Insurance(null, patientId, billId, provider, policy,
                    claim, BigDecimal.ZERO, "SUBMITTED", null,
                    LocalDateTime.now(), null, LocalDateTime.now(), LocalDateTime.now());

            Long id = billingService.submitInsuranceClaim(ins);
            System.out.println("✅ Insurance claim submitted. ID: " + id);
        } catch (Exception e) {
            System.out.println("❌ Error submitting claim: " + e.getMessage());
        }
    }

    private void createPaymentPlan() {
        try {
            System.out.print("Bill ID: ");
            Long billId = Long.parseLong(scanner.nextLine());

            System.out.print("Patient ID: ");
            Long patientId = Long.parseLong(scanner.nextLine());

            System.out.print("Total Amount: ");
            BigDecimal total = new BigDecimal(scanner.nextLine());

            System.out.print("Number of Installments: ");
            int n = Integer.parseInt(scanner.nextLine());

            // Installment amount can be auto-split by service if blank/null
            PaymentPlan plan = new PaymentPlan(null, billId, patientId, total, n, null,
                    "ACTIVE", null, LocalDateTime.now(), null, LocalDateTime.now(), LocalDateTime.now());

            Long id = billingService.createPaymentPlan(plan);
            System.out.println("✅ Payment plan created. ID: " + id);
        } catch (Exception e) {
            System.out.println("❌ Error creating plan: " + e.getMessage());
        }
    }

    private void deleteBill() {
        try {
            System.out.print("Bill ID: ");
            Long billId = Long.parseLong(scanner.nextLine());
            boolean ok = billingService.deleteBill(billId);
            System.out.println(ok ? "✅ Bill deleted." : "❌ Delete failed.");
        } catch (Exception e) {
            System.out.println("❌ " + e.getMessage());
        }
    }
}