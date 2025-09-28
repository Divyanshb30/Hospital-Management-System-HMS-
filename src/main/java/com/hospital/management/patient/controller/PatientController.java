package com.hospital.management.patient.controller;

import com.hospital.management.patient.model.Patient;
import com.hospital.management.patient.service.PatientService;
import com.hospital.management.patient.service.PatientServiceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * CLI Controller for Patient interactions.
 * Handles user input/output and delegates to PatientService.
 */
public class PatientController {

    private final PatientService patientService;
    private final Scanner scanner;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
        this.scanner = new Scanner(System.in);
    }

    /** Convenience ctor using default service impl. */
    public PatientController() {
        this(new PatientServiceImpl());
    }

    public void start() {
        while (true) {
            System.out.println("\n=== Patient Management Menu ===");
            System.out.println("1. Register new patient");
            System.out.println("2. View patient by ID");
            System.out.println("3. View all patients");
            System.out.println("4. Search patients");
            System.out.println("5. Update patient");
            System.out.println("6. Discharge patient");
            System.out.println("7. Delete patient");
            System.out.println("8. Count patients");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");

            int choice = Integer.parseInt(scanner.nextLine().trim());
            switch (choice) {
                case 1 -> registerPatient();
                case 2 -> viewPatientById();
                case 3 -> viewAllPatients();
                case 4 -> searchPatients();
                case 5 -> updatePatient();
                case 6 -> dischargePatient();
                case 7 -> deletePatient();
                case 8 -> countPatients();
                case 0 -> { System.out.println("Exiting Patient Menu..."); return; }
                default -> System.out.println("Invalid choice, try again.");
            }
        }
    }

    private void registerPatient() {
        Patient p = new Patient();
        System.out.print("First name: ");
        p.setFirstName(scanner.nextLine().trim());
        System.out.print("Last name: ");
        p.setLastName(scanner.nextLine().trim());
        System.out.print("Date of Birth (yyyy-mm-dd): ");
        p.setDateOfBirth(LocalDate.parse(scanner.nextLine().trim()));
        System.out.print("Gender (M/F/O): ");
        p.setGender(scanner.nextLine().trim());
        System.out.print("Phone: ");
        p.setPhone(scanner.nextLine().trim());
        System.out.print("Email: ");
        p.setEmail(scanner.nextLine().trim());
        System.out.print("Address: ");
        p.setAddress(scanner.nextLine().trim());
        System.out.print("Blood Group: ");
        p.setBloodGroup(scanner.nextLine().trim());
        p.setStatus("ACTIVE");

        Long id = patientService.registerPatient(p);
        System.out.println("✅ Patient registered with ID: " + id);
    }

    private void viewPatientById() {
        System.out.print("Enter patient ID: ");
        Long id = Long.parseLong(scanner.nextLine().trim());
        Optional<Patient> patient = patientService.getPatientById(id);
        patient.ifPresentOrElse(
                System.out::println,
                () -> System.out.println("❌ Patient not found.")
        );
    }

    private void viewAllPatients() {
        List<Patient> patients = patientService.getAllPatients();
        if (patients.isEmpty()) {
            System.out.println("No patients found.");
        } else {
            patients.forEach(System.out::println);
        }
    }

    private void searchPatients() {
        System.out.print("Enter search keyword: ");
        String keyword = scanner.nextLine().trim();
        List<Patient> patients = patientService.searchPatients(keyword);
        if (patients.isEmpty()) {
            System.out.println("No patients matched your search.");
        } else {
            patients.forEach(System.out::println);
        }
    }

    private void updatePatient() {
        System.out.print("Enter patient ID to update: ");
        Long id = Long.parseLong(scanner.nextLine().trim());
        Optional<Patient> existing = patientService.getPatientById(id);
        if (existing.isEmpty()) {
            System.out.println("❌ Patient not found.");
            return;
        }

        Patient p = existing.get();
        System.out.print("Update phone [" + p.getPhone() + "]: ");
        p.setPhone(scanner.nextLine().trim());
        System.out.print("Update email [" + p.getEmail() + "]: ");
        p.setEmail(scanner.nextLine().trim());

        boolean success = patientService.updatePatient(p);
        System.out.println(success ? "✅ Patient updated." : "❌ Update failed.");
    }

    private void dischargePatient() {
        System.out.print("Enter patient ID to discharge: ");
        Long id = Long.parseLong(scanner.nextLine().trim());
        boolean success = patientService.dischargePatient(id);
        System.out.println(success ? "✅ Patient discharged." : "❌ Discharge failed.");
    }

    private void deletePatient() {
        System.out.print("Enter patient ID to delete: ");
        Long id = Long.parseLong(scanner.nextLine().trim());
        boolean success = patientService.deletePatient(id);
        System.out.println(success ? "✅ Patient deleted." : "❌ Deletion failed.");
    }

    private void countPatients() {
        long count = patientService.getTotalPatients();
        System.out.println("Total registered patients: " + count);
    }
}
