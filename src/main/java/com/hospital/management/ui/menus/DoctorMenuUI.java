package com.hospital.management.ui.menus;

import com.hospital.management.controllers.DoctorController;
import com.hospital.management.services.impl.UserServiceImpl;
import com.hospital.management.services.impl.AppointmentServiceImpl;
import com.hospital.management.ui.InputHandler;
import com.hospital.management.commands.CommandResult;
import com.hospital.management.models.User;
import com.hospital.management.models.Doctor;
import com.hospital.management.models.Appointment;
import com.hospital.management.common.utils.InputValidator;
import com.hospital.management.common.enums.UserRole;

import java.util.Optional;
import java.util.List;

/**
 * Doctor Menu UI with login/logout functionality and proper controller integration
 */
public class DoctorMenuUI {
    private final DoctorController doctorController;
    private final UserServiceImpl userService;
    private final InputHandler input;

    // Session management
    private User currentUser = null;
    private boolean isLoggedIn = false;

    public DoctorMenuUI() {
        this.userService = new UserServiceImpl();
        this.doctorController = new DoctorController(
                userService,
                new AppointmentServiceImpl()
        );
        this.input = InputHandler.getInstance();
    }

    public void show() {
        while (true) {
            if (!isLoggedIn) {
                // Show login menu
                if (!showLoginMenu()) {
                    return; // Back to main menu
                }
            } else {
                // Show doctor dashboard
                if (!showDoctorDashboard()) {
                    return; // Logout and back to main menu
                }
            }
        }
    }

    private boolean showLoginMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("👨‍⚕️ DOCTOR PORTAL");
        System.out.println("=".repeat(50));
        System.out.println("🔑 Please login to access doctor dashboard");
        System.out.println();
        System.out.println("1. 🔑 Login to Account");
        System.out.println("2. ❓ Forgot Password");
        System.out.println("0. ⬅️  Back to Main Menu");
        System.out.println("=".repeat(50));

        int choice = input.getInt("Select an option (0-2): ", 0, 2);

        switch (choice) {
            case 1 -> handleLogin();
            case 2 -> handleForgotPassword();
            case 0 -> { return false; }
            default -> System.out.println("❌ Invalid option.");
        }
        return true;
    }

    private boolean showDoctorDashboard() {
        System.out.println("=".repeat(50));
        System.out.println("🩺 DOCTOR DASHBOARD - Dr. " + currentUser.getUsername());
        System.out.println("=".repeat(50));
        System.out.println("1. 📅 View My Schedule");
        System.out.println("2. 👤 Update Profile");
        System.out.println("5. 💰 View Consultation Fees");
        System.out.println("9. ⚙️ Account Settings");
        System.out.println("0. 🚪 Logout");
        System.out.println("=".repeat(50));

        int choice = input.getInt("Select an option (0-9): ", 0, 9);

        switch (choice) {
            case 1 -> handleViewSchedule();
            case 2 -> handleUpdateProfile();
            case 5 -> handleViewConsultationFees();
            case 9 -> handleAccountSettings();
            case 0 -> {
                handleLogout();
                return false;
            }
            default -> System.out.println("❌ Invalid option.");
        }
        return true;
    }

    private void handleLogin() {
        System.out.println("\n🔑 DOCTOR LOGIN");
        System.out.println("=" .repeat(20));

        try {
            String loginId = input.getString("👤 Doctor ID or Email: ");
            String password = input.getPasswordInput("🔑 Password: ");

            if (loginId.isEmpty() || password.isEmpty()) {
                System.out.println("❌ Login ID and password are required");
                return;
            }

            System.out.println("\n🔄 Authenticating doctor...");

            // Authenticate using UserService
            boolean authenticated = userService.authenticate(loginId, password);

            if (authenticated) {
                // Get user details
                Optional<User> userOpt = userService.findUserByUsername(loginId);
                if (userOpt.isEmpty()) {
                    // Try finding by email if username didn't work
                    userOpt = userService.findAllUsers().stream()
                            .filter(u -> u.getEmail().equals(loginId))
                            .findFirst();
                }

                if (userOpt.isPresent() && userOpt.get().getRole() == UserRole.DOCTOR) {
                    currentUser = userOpt.get();
                    isLoggedIn = true;
                    System.out.println("✅ Doctor login successful!");
                    System.out.println("👨‍⚕️ Welcome Dr. " + currentUser.getUsername() + "!");
                } else {
                    System.out.println("❌ Access denied: Not a doctor account");
                    System.out.println("💡 Please contact admin if you are a registered doctor");
                }
            } else {
                System.out.println("❌ Doctor login failed: Invalid credentials");
                System.out.println("💡 Please check your Doctor ID/Email and password");
            }

        } catch (Exception e) {
            System.out.println("❌ Login error: " + e.getMessage());
        }
    }

    private void handleLogout() {
        System.out.println("\n🔄 Logging out...");
        System.out.println("👋 Goodbye, Dr. " + (currentUser != null ? currentUser.getUsername() : "Doctor") + "!");
        currentUser = null;
        isLoggedIn = false;
        System.out.println("✅ Logged out successfully!");
    }

    private void handleViewSchedule() {
        System.out.println("📅 VIEW MY SCHEDULE");
        System.out.println("═".repeat(20));

        try {
            if (!isLoggedIn || currentUser == null) {
                System.out.println("❌ Please login first");
                return;
            }

            // ✅ FIX: Get the actual doctor ID from database (same as patient fix)
            Long userId = currentUser.getId();
            Long doctorId = getDoctorIdFromDatabase(userId);  // Convert user_id → doctor_id

            if (doctorId == null) {
                System.out.println("❌ Doctor record not found for user ID " + userId);
                return;
            }

            System.out.println("🔍 Loading your schedule...");

            // Call DoctorController with correct doctor_id
            CommandResult result = doctorController.viewSchedule(doctorId);

            if (result.isSuccess()) {
                @SuppressWarnings("unchecked")
                List<Appointment> appointments = (List<Appointment>) result.getData();

                displaySchedule(appointments);
            } else {
                System.out.println("❌ " + result.getMessage());
            }

        } catch (Exception e) {
            System.out.println("❌ Error viewing schedule: " + e.getMessage());
        }
    }

    // ✅ ADD THIS HELPER METHOD
    private Long getDoctorIdFromDatabase(Long userId) {
        try {
            String sql = "SELECT d.id FROM doctors d WHERE d.user_id = ?";
            try (java.sql.Connection conn = com.hospital.management.common.config.DatabaseConfig.getConnection();
                 java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setLong(1, userId);
                java.sql.ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return rs.getLong("id");
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Database error getting doctor ID: " + e.getMessage());
        }
        return null;
    }

    // ✅ ADD THIS DISPLAY METHOD
    private void displaySchedule(List<Appointment> appointments) {
        if (appointments.isEmpty()) {
            System.out.println("📋 No appointments scheduled");
            return;
        }

        System.out.println("\n📅 YOUR SCHEDULE (" + appointments.size() + " appointments)");
        System.out.println("═".repeat(90));

        for (int i = 0; i < appointments.size(); i++) {
            Appointment apt = appointments.get(i);

            // ✅ Get patient name from database
            String patientName = getPatientName(apt.getPatientId());

            System.out.printf("%d. 📅 %s at %s%n", (i + 1), apt.getAppointmentDate(), apt.getAppointmentTime());
            System.out.printf("   👤 Patient: %s (ID: %d)%n", patientName, apt.getPatientId());
            System.out.printf("   📋 Status: %s%n", apt.getStatus());
            System.out.printf("   📝 Reason: %s%n", apt.getReason() != null ? apt.getReason() : "General consultation");

            // ✅ Optional: Show appointment notes if exists
            if (apt.getNotes() != null && !apt.getNotes().trim().isEmpty()) {
                System.out.printf("   📄 Notes: %s%n", apt.getNotes());
            }

            System.out.println("   " + "─".repeat(86));
        }

        System.out.println("═".repeat(90));
        input.getString("Press Enter to continue...");
    }

    // ✅ ADD THIS HELPER METHOD to get patient names
    private String getPatientName(Long patientId) {
        try {
            String sql = "SELECT p.first_name, p.last_name FROM patients p WHERE p.id = ?";
            try (java.sql.Connection conn = com.hospital.management.common.config.DatabaseConfig.getConnection();
                 java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setLong(1, patientId);
                java.sql.ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    return firstName + " " + lastName;
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error getting patient name: " + e.getMessage());
        }
        return "Unknown Patient";
    }



    private void handleUpdateProfile() {
        System.out.println("👤 UPDATE PROFILE");
        System.out.println("═".repeat(20));

        try {
            if (!isLoggedIn || currentUser == null) {
                System.out.println("❌ Please login first");
                return;
            }

            // ✅ FIX: Get the actual doctor ID from database
            Long userId = currentUser.getId();  // This is 6
            Long doctorId = getDoctorIdFromDatabase(userId);  // Convert to doctor_id = 1

            if (doctorId == null) {
                System.out.println("❌ Doctor record not found for user ID " + userId);
                return;
            }

            System.out.println("🔍 Update your profile information (leave blank to keep current value):");
            System.out.println();

            String firstName = input.getString("👤 First Name: ");
            String lastName = input.getString("👤 Last Name: ");
            String email = input.getString("📧 Email: ");
            String phone = input.getString("📞 Phone Number: ");
            String specialization = input.getString("🏥 Specialization: ");

            System.out.println("\n🔄 Updating profile...");

            // ✅ Use doctor_id = 1 instead of user_id = 6
            CommandResult result = doctorController.updateProfile(doctorId, firstName, lastName, email, phone, specialization);

            if (result.isSuccess()) {
                System.out.println("✅ Profile updated successfully!");
            } else {
                System.out.println("❌ Profile update failed: " + result.getMessage());
            }

        } catch (Exception e) {
            System.out.println("❌ Error updating profile: " + e.getMessage());
        }

        input.getString("Press Enter to continue...");
    }


    // Placeholder methods for future implementation
    private void handleForgotPassword() {
        System.out.println("\n🔑 PASSWORD RECOVERY");
        System.out.println("📧 Enter your registered email to receive password reset instructions:");
        String email = input.getString("Email: ");
        System.out.println("✅ Password reset instructions sent to: " + email);
        System.out.println("💡 Check your email for further instructions");
        System.out.println("🚧 Password recovery functionality - Coming soon!");
    }


    private void handleViewConsultationFees() {
        System.out.println("💰 CONSULTATION FEES");
        System.out.println("═".repeat(25));

        try {
            if (!isLoggedIn || currentUser == null) {
                System.out.println("❌ Please login first");
                return;
            }

            // Get doctor ID
            Long userId = currentUser.getId();
            Long doctorId = getDoctorIdFromDatabase(userId);

            if (doctorId == null) {
                System.out.println("❌ Doctor record not found");
                return;
            }

            System.out.println("🔍 Loading your consultation fees...");

            // Get current consultation fee
            CommandResult result = doctorController.getConsultationFees(doctorId);

            if (result.isSuccess()) {
                @SuppressWarnings("unchecked")
                java.math.BigDecimal currentFee = (java.math.BigDecimal) result.getData();

                displayConsultationFeesMenu(doctorId, currentFee);
            } else {
                System.out.println("❌ " + result.getMessage());
            }

        } catch (Exception e) {
            System.out.println("❌ Error loading consultation fees: " + e.getMessage());
        }
    }

    private void displayConsultationFeesMenu(Long doctorId, java.math.BigDecimal currentFee) {
        boolean stayInFeesMenu = true;

        while (stayInFeesMenu) {
            System.out.println("\n💰 CONSULTATION FEES MANAGEMENT");
            System.out.println("═".repeat(35));
            System.out.printf("💵 Current Consultation Fee: ₹%.2f%n", currentFee);
            System.out.println("═".repeat(35));
            System.out.println("1. 📈 Update Consultation Fee");
            System.out.println("2. 📊 View Fee History");
            System.out.println("0. 🔙 Back to Dashboard");
            System.out.println("═".repeat(35));

            String choice = input.getString("Select option (0-2): ");

            switch (choice) {
                case "1":
                    currentFee = handleUpdateConsultationFee(doctorId, currentFee);
                    break;
                case "2":
                    handleViewFeeHistory(doctorId);
                    break;
                case "0":
                    stayInFeesMenu = false;
                    break;
                default:
                    System.out.println("❌ Invalid option");
            }
        }
    }

    private java.math.BigDecimal handleUpdateConsultationFee(Long doctorId, java.math.BigDecimal currentFee) {
        System.out.println("\n📈 UPDATE CONSULTATION FEE");
        System.out.println("─".repeat(30));
        System.out.printf("💵 Current Fee: ₹%.2f%n", currentFee);
        System.out.println("💡 Suggested fees: ₹500, ₹750, ₹1000, ₹1500, ₹2000");
        System.out.println();

        try {
            // Get new fee amount
            String feeInput = input.getString("💰 Enter new consultation fee (₹): ");
            if (feeInput == null || feeInput.trim().isEmpty()) {
                System.out.println("❌ Fee amount is required");
                return currentFee;
            }

            // Validate fee amount
            java.math.BigDecimal newFee;
            try {
                newFee = new java.math.BigDecimal(feeInput.trim());
                if (newFee.compareTo(java.math.BigDecimal.ZERO) <= 0) {
                    System.out.println("❌ Fee must be greater than 0");
                    return currentFee;
                }
                if (newFee.compareTo(new java.math.BigDecimal("10000")) > 0) {
                    System.out.println("❌ Fee cannot exceed ₹10,000");
                    return currentFee;
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number");
                return currentFee;
            }

            // Confirm the change
            System.out.printf("💰 Current Fee: ₹%.2f → New Fee: ₹%.2f%n", currentFee, newFee);
            String confirm = input.getString("✅ Confirm fee update? (y/n): ");

            if (!"y".equalsIgnoreCase(confirm) && !"yes".equalsIgnoreCase(confirm)) {
                System.out.println("❌ Fee update cancelled");
                return currentFee;
            }

            System.out.println("🔄 Updating consultation fee...");

            // Update fee in database
            CommandResult result = doctorController.updateConsultationFee(doctorId, newFee);

            if (result.isSuccess()) {
                System.out.printf("✅ Consultation fee updated successfully to ₹%.2f!%n", newFee);
                System.out.printf("📈 Fee increased by ₹%.2f%n", newFee.subtract(currentFee));
                return newFee;
            } else {
                System.out.println("❌ Failed to update fee: " + result.getMessage());
                return currentFee;
            }

        } catch (Exception e) {
            System.out.println("❌ Error updating fee: " + e.getMessage());
            return currentFee;
        }
    }

    private void handleViewFeeHistory(Long doctorId) {
        System.out.println("\n📊 FEE HISTORY - Coming Soon!");
        System.out.println("This feature will show your consultation fee change history");
        input.getString("Press Enter to continue...");
    }


    private void handleAccountSettings() {
        System.out.println("⚙️ ACCOUNT SETTINGS");
        System.out.println("═".repeat(20));

        try {
            if (!isLoggedIn || currentUser == null) {
                System.out.println("❌ Please login first");
                return;
            }

            // Get doctor ID
            Long userId = currentUser.getId();
            Long doctorId = getDoctorIdFromDatabase(userId);

            if (doctorId == null) {
                System.out.println("❌ Doctor record not found");
                return;
            }

            boolean stayInSettings = true;
            while (stayInSettings) {
                showAccountSettingsMenu();
                String choice = input.getString("Select option (0-2): ");

                switch (choice) {
                    case "1":
                        handleChangePassword();
                        break;
                    case "2":
                        handleChangeQualification(doctorId);
                        break;
                    case "0":
                        stayInSettings = false;
                        break;
                    default:
                        System.out.println("❌ Invalid option");
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Error in account settings: " + e.getMessage());
        }
    }

    private void showAccountSettingsMenu() {
        System.out.println("\n⚙️ ACCOUNT SETTINGS");
        System.out.println("═".repeat(30));
        System.out.println("1. 🔐 Change Password");
        System.out.println("2. 🎓 Update Qualification");
        System.out.println("0. 🔙 Back to Dashboard");
        System.out.println("═".repeat(30));
    }

    private void handleChangePassword() {
        System.out.println("\n🔐 CHANGE PASSWORD");
        System.out.println("─".repeat(20));

        try {
            // Get current password for verification
            String currentPassword = input.getString("🔒 Enter current password: ");
            if (currentPassword == null || currentPassword.trim().isEmpty()) {
                System.out.println("❌ Current password is required");
                return;
            }

            String newPassword = input.getString("🔑 Enter new password: ");
            if (newPassword == null || newPassword.trim().length() < 6) {
                System.out.println("❌ New password must be at least 6 characters long");
                return;
            }

            userService.debugPasswordIssue(currentUser.getUsername(), currentPassword, newPassword);


            // ✅ NOW USE UserService methods
            boolean isValidPassword = userService.verifyPassword(currentUser.getUsername(), currentPassword);
            if (!isValidPassword) {
                System.out.println("❌ Current password is incorrect");
                return;
            }

            // Confirm new password
            String confirmPassword = input.getString("🔑 Confirm new password: ");
            if (!newPassword.equals(confirmPassword)) {
                System.out.println("❌ Passwords do not match");
                return;
            }

            System.out.println("🔄 Updating password...");

            // ✅ NOW USE UserService method
            boolean success = userService.updatePassword(currentUser.getId(), newPassword);

            if (success) {
                System.out.println("✅ Password updated successfully!");
                System.out.println("🔒 Please use your new password for future logins");
            } else {
                System.out.println("❌ Failed to update password");
            }

        } catch (Exception e) {
            System.out.println("❌ Error updating password: " + e.getMessage());
        }

        input.getString("\nPress Enter to continue...");
    }


    private void handleChangeQualification(Long doctorId) {
        System.out.println("\n🎓 UPDATE QUALIFICATION");
        System.out.println("─".repeat(25));

        try {
            // Get current qualification
            System.out.println("💡 Examples: MBBS, MD, MS, BDS, BAMS, BHMS, etc.");
            System.out.println("💡 You can also add specializations: MBBS, MD (Cardiology)");
            System.out.println();

            String newQualification = input.getString("🎓 Enter new qualification: ");
            if (newQualification == null || newQualification.trim().isEmpty()) {
                System.out.println("❌ Qualification cannot be empty");
                return;
            }

            // Validate qualification format
            if (newQualification.trim().length() < 2) {
                System.out.println("❌ Please enter a valid qualification");
                return;
            }

            System.out.println("🔄 Updating qualification...");

            // Update qualification in database
            CommandResult result = doctorController.updateQualification(doctorId, newQualification.trim());

            if (result.isSuccess()) {
                System.out.println("✅ Qualification updated successfully!");
                System.out.printf("🎓 New qualification: %s%n", newQualification.trim());
            } else {
                System.out.println("❌ Failed to update qualification: " + result.getMessage());
            }

        } catch (Exception e) {
            System.out.println("❌ Error updating qualification: " + e.getMessage());
        }

        input.getString("\nPress Enter to continue...");
    }


}
