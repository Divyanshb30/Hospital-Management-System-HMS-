package com.hospital.management;

import com.hospital.management.common.config.DatabaseConfig;

import java.util.Scanner;

/**
 * Hospital Management System - Main Application
 * Team16 Collaborative Development
 */
public class App {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("🏥 Welcome to Hospital Management System!");
        System.out.println("👥 Team16 - Professional Medical Management");
        System.out.println("==========================================");

        // Test database connection on startup
        if (!DatabaseConfig.testConnection()) {
            System.err.println("❌ Database connection failed!");
            System.err.println("💡 Please run migration first:");
            System.err.println("   mvn exec:java -Dexec.mainClass=\"com.hospital.management.common.migration.DatabaseMigrationRunner\"");
            return;
        }

        System.out.println("✅ Database connection successful!");

        // Start main application menu
        showMainMenu();
    }

    private static void showMainMenu() {
        while (true) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("🏥 HOSPITAL MANAGEMENT SYSTEM - MAIN MENU!!");
            System.out.println("=".repeat(50));
            System.out.println("1. Patient Management");
            System.out.println("2. Doctor Management");
            System.out.println("3. Appointment Management (Coming Soon)");
            System.out.println("4. Prescription Management (Coming Soon)");
            System.out.println("5. Billing Management (Coming Soon)");
            System.out.println("6. Reports & Statistics (Coming Soon)");
            System.out.println("0. Exit");
            System.out.println("=".repeat(50));
            System.out.print("Select an option (0-6): ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1 -> {
                        System.out.println("\n🔄 Launching Patient Management System...");

                    }
                    case 2 -> {
                        System.out.println("\n🔄 Launching Doctor Management System...");

                    }
                    case 3 -> {
                        System.out.println("\n🔄 Launching Medical Management System...");

                    }
                    case 4, 5, 6 -> System.out.println("🚧 Feature coming soon in next sprint!");
                    case 0 -> {
                        System.out.println("👋 Thank you for using Hospital Management System!");
                        System.out.println("💡 Stay healthy! - Team16");
                        return;
                    }
                    default -> System.out.println("❌ Invalid option. Please select 0-6.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number (0-6).");
            } catch (Exception e) {
                System.err.println("❌ An error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
