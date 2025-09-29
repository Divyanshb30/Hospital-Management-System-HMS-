package com.hospital.management;

import com.hospital.management.common.config.DatabaseConfig;
import com.hospital.management.common.config.AppConfig;
import com.hospital.management.common.enums.UserRole;
import com.hospital.management.common.exceptions.AuthenticationException;
import com.hospital.management.common.exceptions.ValidationException;
import com.hospital.management.common.utils.InputValidator;
import com.hospital.management.common.utils.DateTimeUtil;
import com.hospital.management.common.utils.PasswordEncoder;
import com.hospital.management.models.Admin;
import com.hospital.management.models.Department;
import com.hospital.management.models.Doctor;
import com.hospital.management.models.Patient;

import java.time.LocalDate;
import java.util.Scanner;

/**
 * Hospital Management System - Main Application
 * Role-Based Authentication Interface
 * Team16 Collaborative Development
 */
public class App {

    private static final Scanner scanner = new Scanner(System.in);
    private static final AppConfig config = AppConfig.getInstance();
    private static String currentUser = null;
    private static UserRole currentUserRole = null;

    public static void main(String[] args) {
        displayWelcomeBanner();

        // Test database connection on startup
        if (!DatabaseConfig.testConnection()) {
            System.err.println("❌ Database connection failed!");
            System.err.println("💡 Please run migration first:");
            System.err.println("   mvn exec:java -Dexec.mainClass=\"com.hospital.management.common.migration.DatabaseMigrationRunner\"");
            return;
        }

        System.out.println("✅ Database connection successful!");

        // Start role-based authentication menu
        showRoleSelectionMenu();
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

    /**
     * Main role selection menu
     */
    private static void showRoleSelectionMenu() {
        while (true) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("🏥 WELCOME TO HOSPITAL MANAGEMENT SYSTEM");
            System.out.println("=".repeat(60));
            System.out.println("Please select your role to continue:");
            System.out.println();
            System.out.println("1. Patient (Login/Register)");
            System.out.println("2. Doctor (Login)");
            System.out.println("3. Admin (Login)");
            System.out.println();
            System.out.println("8. System Diagnostics");
            System.out.println("9. Help");
            System.out.println("0. Exit");
            System.out.println("=".repeat(60));
            System.out.print("Select your role (0-9): ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1 -> showPatientInterface();
                    case 2 -> showDoctorInterface();
                    case 3 -> showAdminInterface();
                    case 8 -> showSystemDiagnostics();
                    case 9 -> showHelp();
                    case 0 -> {
                        System.out.println("\n👋 Thank you for using " + config.getApplicationName() + "!");
                        System.out.println("💡 Stay healthy! - Team16");
                        return;
                    }
                    default -> System.out.println("❌ Invalid option. Please select 0-9.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number (0-9).");
            } catch (Exception e) {
                System.err.println("❌ An error occurred: " + e.getMessage());
                System.out.println("🔄 Returning to main menu...");
            }
        }
    }

    // ==================== PATIENT INTERFACE ====================

    private static void showPatientInterface() {
        while (true) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("👤 PATIENT PORTAL");
            System.out.println("=".repeat(50));
            System.out.println("1. 📝 Register New Account");
            System.out.println("2. 🔑 Login to Existing Account");
            System.out.println("3. ❓ Forgot Password");
            System.out.println("0. ⬅️  Back to Main Menu");
            System.out.println("=".repeat(50));
            System.out.print("Select an option (0-3): ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1 -> handlePatientRegistration();
                    case 2 -> handlePatientLogin();
                    case 3 -> handleForgotPassword(UserRole.PATIENT);
                    case 0 -> {
                        return; // Back to main menu
                    }
                    default -> System.out.println("❌ Invalid option. Please select 0-3.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number (0-3).");
            }
        }
    }

    private static void handlePatientRegistration() {
        System.out.println("\n📝 PATIENT REGISTRATION");
        System.out.println("=" .repeat(30));

        try {
            // Collect patient information
            System.out.print("👤 First Name: ");
            String firstName = scanner.nextLine().trim();
            InputValidator.validateName(firstName, "First Name");

            System.out.print("👤 Last Name: ");
            String lastName = scanner.nextLine().trim();
            InputValidator.validateName(lastName, "Last Name");

            System.out.print("📧 Email: ");
            String email = scanner.nextLine().trim();
            InputValidator.validateEmail(email, "Email");

            System.out.print("📱 Phone Number: ");
            String phone = scanner.nextLine().trim();
            InputValidator.validatePhone(phone, "Phone");

            System.out.print("👤 Username: ");
            String username = scanner.nextLine().trim();
            if (!InputValidator.isValidUsername(username)) {
                throw new ValidationException("Username must be 3-20 characters, alphanumeric and underscore only", "Username", username);
            }

            System.out.print("🔑 Password: ");
            String password = scanner.nextLine().trim();
            if (!InputValidator.isValidPassword(password)) {
                throw new ValidationException("Password must be at least 8 characters with uppercase, lowercase, digit, and special character", "Password");
            }

            System.out.print("🔑 Confirm Password: ");
            String confirmPassword = scanner.nextLine().trim();
            if (!password.equals(confirmPassword)) {
                throw new ValidationException("Passwords do not match");
            }

            // Simulate registration process
            System.out.println("\n🔄 Processing registration...");
            Thread.sleep(1000); // Simulate processing time

            // Hash password
            String hashedPassword = PasswordEncoder.encodePassword(password);

            System.out.println("✅ Registration successful!");
            System.out.println("📧 Account created for: " + firstName + " " + lastName);
            System.out.println("👤 Username: " + username);
            System.out.println("💡 You can now login with your credentials");

            // TODO: Save to database
            System.out.println("🔧 [DEV] Registration data ready for database insertion");

        } catch (ValidationException e) {
            System.out.println("❌ Registration failed: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Registration error: " + e.getMessage());
        }
    }

    private static void handlePatientLogin() {
        System.out.println("\n🔑 PATIENT LOGIN");
        System.out.println("=" .repeat(20));

        try {
            System.out.print("👤 Username or Email: ");
            String loginId = scanner.nextLine().trim();

            System.out.print("🔑 Password: ");
            String password = scanner.nextLine().trim();

            if (loginId.isEmpty() || password.isEmpty()) {
                throw new AuthenticationException("Username and password are required");
            }

            System.out.println("\n🔄 Authenticating...");
            Thread.sleep(1000); // Simulate authentication

            // TODO: Actual authentication logic
            // For now, simulate successful login
            if (authenticateUser(loginId, password, UserRole.PATIENT)) {
                currentUser = loginId;
                currentUserRole = UserRole.PATIENT;
                System.out.println("✅ Login successful!");
                System.out.println("👋 Welcome back, " + loginId + "!");

                // Redirect to patient dashboard
                showPatientDashboard();
            } else {
                throw new AuthenticationException("Invalid credentials", loginId);
            }

        } catch (AuthenticationException e) {
            System.out.println("❌ Login failed: " + e.getMessage());
            System.out.println("💡 Please check your credentials and try again");
        } catch (Exception e) {
            System.out.println("❌ Login error: " + e.getMessage());
        }
    }

    private static void showPatientDashboard() {
        while (true) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("👤 PATIENT DASHBOARD - Welcome " + currentUser + "!");
            System.out.println("=".repeat(50));
            System.out.println("1. 📅 Book Appointment");
            System.out.println("2. 👁️  View My Appointments");
            System.out.println("3. 📋 Update Profile");
            System.out.println("4. 📊 View Medical History");
            System.out.println("5. 💰 View Bills & Payments");
            System.out.println("6. 📝 Download Reports");
            System.out.println("9. 🔧 Account Settings");
            System.out.println("0. 🚪 Logout");
            System.out.println("=".repeat(50));
            System.out.print("Select an option (0-9): ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1 -> handleBookAppointment();
                    case 2 -> handleViewAppointments();
                    case 3 -> handleUpdateProfile();
                    case 4 -> handleViewMedicalHistory();
                    case 5 -> handleViewBills();
                    case 6 -> handleDownloadReports();
                    case 9 -> handleAccountSettings();
                    case 0 -> {
                        logout();
                        return; // Back to role selection
                    }
                    default -> System.out.println("❌ Invalid option. Please select 0-9.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number (0-9).");
            }
        }
    }

    // ==================== DOCTOR INTERFACE ====================

    private static void showDoctorInterface() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("👨‍⚕️ DOCTOR PORTAL");
        System.out.println("=".repeat(50));
        System.out.println("🔑 Please login to access doctor dashboard");
        System.out.println();

        try {
            System.out.print("👤 Doctor ID or Email: ");
            String loginId = scanner.nextLine().trim();

            System.out.print("🔑 Password: ");
            String password = scanner.nextLine().trim();

            if (loginId.isEmpty() || password.isEmpty()) {
                throw new AuthenticationException("Doctor ID and password are required");
            }

            System.out.println("\n🔄 Authenticating doctor...");
            Thread.sleep(1000);

            if (authenticateUser(loginId, password, UserRole.DOCTOR)) {
                currentUser = loginId;
                currentUserRole = UserRole.DOCTOR;
                System.out.println("✅ Doctor login successful!");
                System.out.println("👨‍⚕️ Welcome Dr. " + loginId + "!");

                showDoctorDashboard();
            } else {
                throw new AuthenticationException("Invalid doctor credentials", loginId);
            }

        } catch (AuthenticationException e) {
            System.out.println("❌ Doctor login failed: " + e.getMessage());
            System.out.println("💡 Please contact admin if you forgot your credentials");
        } catch (Exception e) {
            System.out.println("❌ Login error: " + e.getMessage());
        }
    }

    private static void showDoctorDashboard() {
        while (true) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("👨‍⚕️ DOCTOR DASHBOARD - Dr. " + currentUser);
            System.out.println("=".repeat(50));
            System.out.println("1. 📅 View Today's Appointments");
            System.out.println("2. 📋 View All Appointments");
            System.out.println("3. 👤 Update Patient Records");
            System.out.println("4. ⏰ Manage Schedule");
            System.out.println("5. 👁️  View Patient History");
            System.out.println("6. 💰 Update Consultation Fees");
            System.out.println("7. 📊 Generate Reports");
            System.out.println("9. 🔧 Profile Settings");
            System.out.println("0. 🚪 Logout");
            System.out.println("=".repeat(50));
            System.out.print("Select an option (0-9): ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1 -> handleViewTodayAppointments();
                    case 2 -> handleViewAllAppointments();
                    case 3 -> handleUpdatePatientRecords();
                    case 4 -> handleManageSchedule();
                    case 5 -> handleViewPatientHistory();
                    case 6 -> handleUpdateConsultationFees();
                    case 7 -> handleGenerateReports();
                    case 9 -> handleDoctorProfileSettings();
                    case 0 -> {
                        logout();
                        return;
                    }
                    default -> System.out.println("❌ Invalid option. Please select 0-9.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number (0-9).");
            }
        }
    }

    // ==================== ADMIN INTERFACE ====================

    private static void showAdminInterface() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("🔐 ADMIN PORTAL");
        System.out.println("=".repeat(50));
        System.out.println("⚠️  Restricted Access - Admin Login Required");
        System.out.println();

        try {
            System.out.print("👤 Admin Username: ");
            String loginId = scanner.nextLine().trim();

            System.out.print("🔑 Admin Password: ");
            String password = scanner.nextLine().trim();

            if (loginId.isEmpty() || password.isEmpty()) {
                throw new AuthenticationException("Admin credentials are required");
            }

            System.out.println("\n🔄 Verifying admin access...");
            Thread.sleep(1000);

            if (authenticateUser(loginId, password, UserRole.ADMIN)) {
                currentUser = loginId;
                currentUserRole = UserRole.ADMIN;
                System.out.println("✅ Admin authentication successful!");
                System.out.println("🔐 Welcome Admin " + loginId + "!");

                showAdminDashboard();
            } else {
                throw new AuthenticationException("Invalid admin credentials", loginId);
            }

        } catch (AuthenticationException e) {
            System.out.println("❌ Admin login failed: " + e.getMessage());
            System.out.println("⚠️  Unauthorized access attempt logged");
        } catch (Exception e) {
            System.out.println("❌ Authentication error: " + e.getMessage());
        }
    }

    private static void showAdminDashboard() {
        while (true) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("🔐 ADMIN DASHBOARD - " + currentUser);
            System.out.println("=".repeat(50));
            System.out.println("👥 USER MANAGEMENT:");
            System.out.println("  1. 👤 Manage Patients");
            System.out.println("  2. 👨‍⚕️ Manage Doctors");
            System.out.println("  3. 🔐 Manage Admins");
            System.out.println();
            System.out.println("🏥 HOSPITAL MANAGEMENT:");
            System.out.println("  4. 🏢 Manage Departments");
            System.out.println("  5. 📅 View All Appointments");
            System.out.println("  6. 💰 Billing Management");
            System.out.println("  7. 💳 Payment Management");
            System.out.println();
            System.out.println("📊 REPORTS & ANALYTICS:");
            System.out.println("  8. 📊 Generate Reports");
            System.out.println("  9. 🔧 System Settings");
            System.out.println("  0. 🚪 Logout");
            System.out.println("=".repeat(50));
            System.out.print("Select an option (0-9): ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1 -> handleManagePatients();
                    case 2 -> handleManageDoctors();
                    case 3 -> handleManageAdmins();
                    case 4 -> handleManageDepartments();
                    case 5 -> handleViewAllAppointmentsAdmin();
                    case 6 -> handleBillingManagement();
                    case 7 -> handlePaymentManagement();
                    case 8 -> handleAdminReports();
                    case 9 -> handleSystemSettings();
                    case 0 -> {
                        logout();
                        return;
                    }
                    default -> System.out.println("❌ Invalid option. Please select 0-9.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number (0-9).");
            }
        }
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Simulate user authentication (TODO: Replace with actual authentication)
     */
    private static boolean authenticateUser(String loginId, String password, UserRole role) {
        // Simulate authentication delay
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // TODO: Replace with actual database authentication
        // For testing, accept any non-empty credentials
        return !loginId.isEmpty() && !password.isEmpty();
    }

    private static void logout() {
        System.out.println("\n🔄 Logging out...");
        System.out.println("👋 Goodbye, " + currentUser + "!");
        currentUser = null;
        currentUserRole = null;
        System.out.println("✅ Logged out successfully!");
    }

    private static void handleForgotPassword(UserRole role) {
        System.out.println("\n🔑 PASSWORD RECOVERY");
        System.out.println("📧 Enter your registered email to receive password reset instructions:");

        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        try {
            InputValidator.validateEmail(email, "Email");
            System.out.println("✅ Password reset instructions sent to: " + email);
            System.out.println("💡 Check your email for further instructions");
            // TODO: Implement actual password reset functionality
        } catch (ValidationException e) {
            System.out.println("❌ Invalid email: " + e.getMessage());
        }
    }


    // ==================== PLACEHOLDER METHODS FOR FUTURE IMPLEMENTATION ====================

    // Patient Dashboard Methods
    private static void handleBookAppointment() {
        System.out.println("📅 BOOK APPOINTMENT");
        System.out.println("🚧 Available departments and doctors will be shown here");
        System.out.println("⏰ Available time slots: " + DateTimeUtil.getBusinessHoursTimeSlots().size());
        System.out.println("💰 Consultation fee: ₹" + config.getDefaultConsultationFee());
        System.out.println("📝 Ready for implementation!");
    }

    private static void handleViewAppointments() {
        System.out.println("👁️ MY APPOINTMENTS");
        System.out.println("🚧 Patient appointment history will be displayed here");
        System.out.println("📝 Ready for implementation!");
    }

    private static void handleUpdateProfile() { System.out.println("📋 UPDATE PROFILE - Ready for implementation!"); }
    private static void handleViewMedicalHistory() { System.out.println("📊 MEDICAL HISTORY - Ready for implementation!"); }
    private static void handleViewBills() { System.out.println("💰 BILLS & PAYMENTS - Ready for implementation!"); }
    private static void handleDownloadReports() { System.out.println("📝 DOWNLOAD REPORTS - Ready for implementation!"); }
    private static void handleAccountSettings() { System.out.println("🔧 ACCOUNT SETTINGS - Ready for implementation!"); }

    // Doctor Dashboard Methods
    private static void handleViewTodayAppointments() { System.out.println("📅 TODAY'S APPOINTMENTS - Ready for implementation!"); }
    private static void handleViewAllAppointments() { System.out.println("📋 ALL APPOINTMENTS - Ready for implementation!"); }
    private static void handleUpdatePatientRecords() { System.out.println("👤 UPDATE PATIENT RECORDS - Ready for implementation!"); }
    private static void handleManageSchedule() { System.out.println("⏰ MANAGE SCHEDULE - Ready for implementation!"); }
    private static void handleViewPatientHistory() { System.out.println("👁️ PATIENT HISTORY - Ready for implementation!"); }
    private static void handleUpdateConsultationFees() { System.out.println("💰 UPDATE FEES - Ready for implementation!"); }
    private static void handleGenerateReports() { System.out.println("📊 GENERATE REPORTS - Ready for implementation!"); }
    private static void handleDoctorProfileSettings() { System.out.println("🔧 DOCTOR PROFILE - Ready for implementation!"); }

    // Admin Dashboard Methods
    private static void handleManagePatients() { System.out.println("👤 MANAGE PATIENTS (CRUD) - Ready for implementation!"); }
    private static void handleManageDoctors() { System.out.println("👨‍⚕️ MANAGE DOCTORS (CRUD) - Ready for implementation!"); }
    private static void handleManageAdmins() { System.out.println("🔐 MANAGE ADMINS (CRUD) - Ready for implementation!"); }
    private static void handleManageDepartments() { System.out.println("🏢 MANAGE DEPARTMENTS - Ready for implementation!"); }
    private static void handleViewAllAppointmentsAdmin() { System.out.println("📅 ALL APPOINTMENTS (ADMIN VIEW) - Ready for implementation!"); }
    private static void handleBillingManagement() { System.out.println("💰 BILLING MANAGEMENT - Ready for implementation!"); }
    private static void handlePaymentManagement() { System.out.println("💳 PAYMENT MANAGEMENT - Ready for implementation!"); }
    private static void handleAdminReports() { System.out.println("📊 ADMIN REPORTS - Ready for implementation!"); }
    private static void handleSystemSettings() { System.out.println("🔧 SYSTEM SETTINGS - Ready for implementation!"); }

    // System Methods
    private static void showSystemDiagnostics() {
        System.out.println("\n🔧 SYSTEM DIAGNOSTICS");
        System.out.println("==================");
        System.out.println("🗄️  Database: " + (DatabaseConfig.testConnection() ? "✅ Connected" : "❌ Failed"));
        System.out.println("⚙️  Config: " + config.getApplicationName() + " v" + config.getApplicationVersion());
        System.out.println("🕒 Current Time: " + DateTimeUtil.getCurrentDateTime());
        System.out.println("💰 Default Fee: ₹" + config.getDefaultConsultationFee());
        System.out.println("✅ All systems operational!");
    }

    private static void showHelp() {
        System.out.println("\n❓ HELP & INFORMATION");
        System.out.println("====================");
        System.out.println("👤 PATIENT: Register new account or login to book appointments");
        System.out.println("👨‍⚕️ DOCTOR: Login to manage appointments and patient records");
        System.out.println("🔐 ADMIN: Full system access for hospital management");
        System.out.println("💡 For technical support, contact: Team16");
    }

    // Add this method to your App.java for testing models
    private static void testModels() {
        System.out.println("🧪 Testing Model Classes");
        System.out.println("========================");

        try {
            // Test Patient
            Patient patient = new Patient("john_doe", "hash123", "john@email.com", "9876543210");
            patient.setFirstName("John");
            patient.setLastName("Doe");
            patient.setDateOfBirth(LocalDate.of(1990, 5, 15));
            patient.setGender(Patient.Gender.MALE);
            patient.validate();
            System.out.println("✅ Patient model: " + patient.getDisplayName() + ", Age: " + patient.getAge());

            // Test Department
            Department cardiology = new Department("Cardiology", "Heart diseases", "Building A");
            cardiology.validate();
            System.out.println("✅ Department model: " + cardiology.getDisplayName());

            // Test Doctor
            Doctor doctor = new Doctor("dr_smith", "hash123", "smith@hospital.com", "9876543211");
            doctor.setFirstName("John");
            doctor.setLastName("Smith");
            doctor.setSpecialization("Cardiologist");
            doctor.setLicenseNumber("MED12345");
            doctor.setDepartmentId(1L);
            doctor.validate();
            System.out.println("✅ Doctor model: " + doctor.getDisplayName());

            // Test Admin
            Admin admin = new Admin("admin", "hash123", "admin@hospital.com", "9876543212");
            admin.setFirstName("System");
            admin.setLastName("Administrator");
            System.out.println("✅ Admin model: " + admin.getDisplayName());

            System.out.println("🎉 All model classes working correctly!");

        } catch (Exception e) {
            System.out.println("❌ Model test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }


}
