package com.hospital.management.appointment.controller;

import com.hospital.management.appointment.model.Appointment;
import com.hospital.management.appointment.service.AppointmentScheduler;
import com.hospital.management.appointment.service.AppointmentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

/**
 * AppointmentController
 * ---------------------
 * CLI handler for appointment operations.
 * Connects user input/output with AppointmentService & AppointmentScheduler.
 */
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final AppointmentScheduler scheduler;
    private final Scanner scanner;

    public AppointmentController(AppointmentService appointmentService, AppointmentScheduler scheduler) {
        this.appointmentService = appointmentService;
        this.scheduler = scheduler;
        this.scanner = new Scanner(System.in);
    }

    /** Show main menu loop for appointment management */
    public void startMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== Appointment Management ===");
            System.out.println("1. Schedule new appointment");
            System.out.println("2. List all appointments");
            System.out.println("3. View appointment by ID");
            System.out.println("4. Cancel appointment");
            System.out.println("5. Complete appointment");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    scheduleAppointment();
                    break;
                case "2":
                    listAppointments();
                    break;
                case "3":
                    viewAppointment();
                    break;
                case "4":
                    cancelAppointment();
                    break;
                case "5":
                    completeAppointment();
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice, try again.");
            }
        }
    }

    // --- Menu actions ---

    private void scheduleAppointment() {
        try {
            System.out.print("Enter Patient ID: ");
            Long patientId = Long.parseLong(scanner.nextLine());

            System.out.print("Enter Doctor ID: ");
            Long doctorId = Long.parseLong(scanner.nextLine());

            System.out.print("Enter Start Time (yyyy-MM-ddTHH:mm): ");
            LocalDateTime start = LocalDateTime.parse(scanner.nextLine());

            System.out.print("Enter Notes: ");
            String notes = scanner.nextLine();

            // Use async scheduler
            CompletableFuture<Long> future = scheduler.submit(doctorId, patientId, start, notes);
            future.thenAccept(id ->
                            System.out.println("✅ Appointment scheduled with ID: " + id))
                    .exceptionally(ex -> {
                        System.err.println("❌ Failed to schedule: " + ex.getMessage());
                        return null;
                    });

            System.out.println("⏳ Scheduling in background...");
        } catch (Exception e) {
            System.out.println("❌ Invalid input: " + e.getMessage());
        }
    }

    private void listAppointments() {
        List<Appointment> list = appointmentService.listAll();
        if (list.isEmpty()) {
            System.out.println("No appointments found.");
        } else {
            System.out.println("=== Appointments ===");
            list.forEach(System.out::println);
        }
    }

    private void viewAppointment() {
        try {
            System.out.print("Enter Appointment ID: ");
            Long id = Long.parseLong(scanner.nextLine());
            Optional<Appointment> appt = appointmentService.getById(id);
            appt.ifPresentOrElse(
                    System.out::println,
                    () -> System.out.println("No appointment found with ID " + id));
        } catch (Exception e) {
            System.out.println("❌ Invalid input: " + e.getMessage());
        }
    }

    private void cancelAppointment() {
        try {
            System.out.print("Enter Appointment ID to cancel: ");
            Long id = Long.parseLong(scanner.nextLine());
            System.out.print("Reason: ");
            String reason = scanner.nextLine();
            boolean success = appointmentService.cancel(id, reason);
            System.out.println(success ? "✅ Appointment cancelled." : "❌ Cancel failed.");
        } catch (Exception e) {
            System.out.println("❌ Invalid input: " + e.getMessage());
        }
    }

    private void completeAppointment() {
        try {
            System.out.print("Enter Appointment ID to complete: ");
            Long id = Long.parseLong(scanner.nextLine());
            System.out.print("Remarks: ");
            String remarks = scanner.nextLine();
            boolean success = appointmentService.complete(id, remarks);
            System.out.println(success ? "✅ Appointment completed." : "❌ Completion failed.");
        } catch (Exception e) {
            System.out.println("❌ Invalid input: " + e.getMessage());
        }
    }
}
