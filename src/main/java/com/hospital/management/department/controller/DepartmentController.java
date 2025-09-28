package com.hospital.management.department.controller;

import com.hospital.management.department.model.Department;
import com.hospital.management.department.service.DepartmentService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * DepartmentController
 * --------------------
 * Handles CLI interactions for department management.
 * Connects user input/output with DepartmentService.
 */
public class DepartmentController {

    private final DepartmentService departmentService;
    private final Scanner scanner;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
        this.scanner = new Scanner(System.in);
    }

    /** Start menu loop for department operations */
    public void startMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== Department Management ===");
            System.out.println("1. Create Department");
            System.out.println("2. List All Departments");
            System.out.println("3. View Department by ID");
            System.out.println("4. Search Department by Name");
            System.out.println("5. Update Department");
            System.out.println("6. Update Capacity");
            System.out.println("7. Delete Department");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1": createDepartment(); break;
                case "2": listDepartments(); break;
                case "3": viewDepartment(); break;
                case "4": searchDepartment(); break;
                case "5": updateDepartment(); break;
                case "6": updateCapacity(); break;
                case "7": deleteDepartment(); break;
                case "0": running = false; break;
                default: System.out.println("❌ Invalid choice, try again.");
            }
        }
    }

    private void createDepartment() {
        try {
            System.out.print("Enter Department Name: ");
            String name = scanner.nextLine();

            System.out.print("Enter Description: ");
            String description = scanner.nextLine();

            System.out.print("Enter Type (e.g., CARDIOLOGY): ");
            String type = scanner.nextLine();

            System.out.print("Enter Capacity: ");
            int capacity = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter Head Doctor ID (or leave blank): ");
            String headInput = scanner.nextLine();
            Long headDoctorId = headInput.isBlank() ? null : Long.parseLong(headInput);

            Department dept = new Department(null, name, description, type,
                    capacity, headDoctorId, null, null);

            Long id = departmentService.createDepartment(dept);
            System.out.println("✅ Department created with ID: " + id);

        } catch (Exception e) {
            System.out.println("❌ Error creating department: " + e.getMessage());
        }
    }

    private void listDepartments() {
        List<Department> list = departmentService.listAll();
        if (list.isEmpty()) {
            System.out.println("No departments found.");
        } else {
            list.forEach(System.out::println);
        }
    }

    private void viewDepartment() {
        try {
            System.out.print("Enter Department ID: ");
            Long id = Long.parseLong(scanner.nextLine());
            Optional<Department> dept = departmentService.getById(id);
            dept.ifPresentOrElse(
                    System.out::println,
                    () -> System.out.println("❌ No department found with ID " + id));
        } catch (Exception e) {
            System.out.println("❌ Invalid input: " + e.getMessage());
        }
    }

    private void searchDepartment() {
        System.out.print("Enter name query: ");
        String query = scanner.nextLine();
        List<Department> list = departmentService.searchByName(query);
        if (list.isEmpty()) {
            System.out.println("No matches found.");
        } else {
            list.forEach(System.out::println);
        }
    }

    private void updateDepartment() {
        try {
            System.out.print("Enter Department ID to update: ");
            Long id = Long.parseLong(scanner.nextLine());

            System.out.print("Enter New Name: ");
            String name = scanner.nextLine();

            System.out.print("Enter New Description: ");
            String description = scanner.nextLine();

            System.out.print("Enter New Type: ");
            String type = scanner.nextLine();

            System.out.print("Enter New Capacity: ");
            int capacity = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter New Head Doctor ID (or leave blank): ");
            String headInput = scanner.nextLine();
            Long headDoctorId = headInput.isBlank() ? null : Long.parseLong(headInput);

            Department dept = new Department(id, name, description, type,
                    capacity, headDoctorId, null, null);

            boolean updated = departmentService.updateDepartment(dept);
            System.out.println(updated ? "✅ Department updated." : "❌ Update failed.");

        } catch (Exception e) {
            System.out.println("❌ Error updating department: " + e.getMessage());
        }
    }

    private void updateCapacity() {
        try {
            System.out.print("Enter Department ID: ");
            Long id = Long.parseLong(scanner.nextLine());

            System.out.print("Enter New Capacity: ");
            int capacity = Integer.parseInt(scanner.nextLine());

            boolean success = departmentService.updateCapacity(id, capacity);
            System.out.println(success ? "✅ Capacity updated." : "❌ Capacity update failed.");

        } catch (Exception e) {
            System.out.println("❌ Invalid input: " + e.getMessage());
        }
    }

    private void deleteDepartment() {
        try {
            System.out.print("Enter Department ID: ");
            Long id = Long.parseLong(scanner.nextLine());

            boolean deleted = departmentService.deleteDepartment(id);
            System.out.println(deleted ? "✅ Department deleted." : "❌ Delete failed.");

        } catch (Exception e) {
            System.out.println("❌ Invalid input: " + e.getMessage());
        }
    }
}
