package com.hospital.management;

import com.hospital.management.controllers.PatientController;
import com.hospital.management.services.impl.BillingServiceImpl;
import com.hospital.management.services.impl.PaymentServiceImpl;
import com.hospital.management.services.impl.UserServiceImpl;
import com.hospital.management.services.impl.AppointmentServiceImpl;
import com.hospital.management.ui.menus.PatientMenuUI;
import com.hospital.management.ui.menus.DoctorMenuUI;
import com.hospital.management.ui.menus.AdminMenuUI;        // ✅ Add AdminMenuUI import
import com.hospital.management.ui.InputHandler;
import com.hospital.management.common.config.DatabaseConfig;
import com.hospital.management.common.config.AppConfig;
import com.hospital.management.common.utils.DateTimeUtil;

/**
 * Hospital Management System - Entry Point
 * Team16 Collaborative Development
 */
public class App {

    private static final AppConfig config = AppConfig.getInstance();
    private static InputHandler input;
    private static PatientController patientController;

    public static void main(String[] args) {
        if (!initialize()) {
            System.exit(1);
        }

        showMainMenu();
        System.out.println("\n👋 Thank you for using " + config.getApplicationName() + "!");
        System.out.println("💡 Stay healthy! - Team16");
    }

    private static boolean initialize() {
        displayWelcomeBanner();

        if (!DatabaseConfig.testConnection()) {
            System.err.println("❌ Database connection failed!");
            System.err.println("💡 Please run migration first:");
            System.err.println(" mvn exec:java -Dexec.mainClass=\"com.hospital.management.common.migration.DatabaseMigrationRunner\"");
            return false;
        }

        System.out.println("✅ Database connection successful!");
        input = InputHandler.getInstance();

        // ✅ UPDATED: Initialize PatientController with all required services
        patientController = new PatientController(
                new UserServiceImpl(),      // Real UserService
                new AppointmentServiceImpl(), // Real AppointmentService
                new BillingServiceImpl(),   // ✅ ADD BillingService
                new PaymentServiceImpl()    // ✅ ADD PaymentService
        );

        System.out.println("✅ Services initialized successfully!");
        return true;
    }


    private static void displayWelcomeBanner() {
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║           🏥 HOSPITAL MANAGEMENT SYSTEM 🏥           ║");
        System.out.println("║                                                       ║");
        System.out.println("║              Professional Medical Management          ║");
        System.out.println("║                     Team16 Project                    ║");
        System.out.println("║                                                       ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        System.out.println("📅 Current Time: " + DateTimeUtil.getCurrentDateTime());
        System.out.println("📋 Version: " + config.getApplicationVersion());
        System.out.println();
    }

    private static void showMainMenu() {
        while (true) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("🏥 WELCOME TO HOSPITAL MANAGEMENT SYSTEM");
            System.out.println("=".repeat(60));
            System.out.println("Please select your role to continue:");
            System.out.println();
            System.out.println("1. Patient Portal (Login/Register)");
            System.out.println("2. Doctor Portal (Login)");
            System.out.println("3. Admin Portal (Login)");
            System.out.println();
            System.out.println("8. System Diagnostics");
            System.out.println("9. Help");
            System.out.println("0. Exit");
            System.out.println("=".repeat(60));

            int choice = input.getInt("Select your role (0-9): ", 0, 9);

            switch (choice) {
                case 1 -> {
                    // ✅ Patient Portal - with PatientController
                    PatientMenuUI patientMenu = new PatientMenuUI(patientController);
                    patientMenu.show();
                }
                case 2 -> {
                    // ✅ Doctor Portal - with DoctorController (self-contained)
                    DoctorMenuUI doctorMenu = new DoctorMenuUI();
                    doctorMenu.show();
                }
                case 3 -> {
                    // ✅ Admin Portal - with AdminController (self-contained)
                    AdminMenuUI adminMenu = new AdminMenuUI();
                    adminMenu.show();
                }
                case 8 -> showSystemDiagnostics();
                case 9 -> showHelp();
                case 0 -> { return; }
                default -> System.out.println("❌ Invalid option. Please select 0-9.");
            }
        }
    }

    private static void showSystemDiagnostics() {
        System.out.println("\n🔧 SYSTEM DIAGNOSTICS");
        System.out.println("==================");

        // Database connectivity test
        boolean dbConnected = DatabaseConfig.testConnection();
        System.out.println("🗄️  Database: " + (dbConnected ? "✅ Connected" : "❌ Failed"));

        // Application configuration
        System.out.println("⚙️  Config: " + config.getApplicationName() + " v" + config.getApplicationVersion());
        System.out.println("🕒 Current Time: " + DateTimeUtil.getCurrentDateTime());
        System.out.println("💰 Default Consultation Fee: ₹" + config.getDefaultConsultationFee());

        // Service status
        System.out.println("🔧 Services: " + (patientController != null ? "✅ Initialized" : "❌ Not Ready"));

        // System status
        if (dbConnected && patientController != null) {
            System.out.println("🎉 System Status: ✅ All systems operational!");
        } else {
            System.out.println("⚠️  System Status: ❌ Some components not ready");
        }

        // Memory usage
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;

        System.out.println("💾 Memory Usage: " + (usedMemory / 1024 / 1024) + " MB / " + (totalMemory / 1024 / 1024) + " MB");
    }

    private static void showHelp() {
        System.out.println("\n❓ HELP & INFORMATION");
        System.out.println("====================");
        System.out.println("👤 PATIENT: Register new account or login to:");
        System.out.println("   • Book appointments with doctors");
        System.out.println("   • View your appointment history");
        System.out.println("   • Update your profile information");
        System.out.println();
        System.out.println("👨‍⚕️ DOCTOR: Login with your credentials to:");
        System.out.println("   • View your daily schedule");
        System.out.println("   • Update your profile and specialization");
        System.out.println("   • Manage patient appointments");
        System.out.println();
        System.out.println("🔐 ADMIN: Full system access for:");
        System.out.println("   • User management and reports");
        System.out.println("   • Hospital system administration");
        System.out.println("   • Generate comprehensive reports");
        System.out.println("   • Manage system users and settings");
        System.out.println();
        System.out.println("💡 For technical support, contact: Team16");
        System.out.println("📧 Hospital Management System v" + config.getApplicationVersion());
    }
}
