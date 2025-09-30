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
        System.out.println("\n" + "=".repeat(50));
        System.out.println("👨‍⚕️ DOCTOR DASHBOARD - Dr. " + currentUser.getUsername());
        System.out.println("=".repeat(50));
        System.out.println("1. 📅 View My Schedule");
        System.out.println("2. 📋 Update Profile");
        System.out.println("3. 👤 View Patient Records");
        System.out.println("4. ⏰ Manage Availability");
        System.out.println("5. 💰 View Consultation Fees");
        System.out.println("6. 📊 Generate Reports");
        System.out.println("9. 🔧 Account Settings");
        System.out.println("0. 🚪 Logout");
        System.out.println("=".repeat(50));

        int choice = input.getInt("Select an option (0-9): ", 0, 9);

        switch (choice) {
            case 1 -> handleViewSchedule();
            case 2 -> handleUpdateProfile();
            case 3 -> handleViewPatientRecords();
            case 4 -> handleManageAvailability();
            case 5 -> handleViewConsultationFees();
            case 6 -> handleGenerateReports();
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
            String password = input.getString("🔑 Password: ");

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
        System.out.println("\n📅 VIEW MY SCHEDULE");
        System.out.println("=" .repeat(25));

        try {
            if (!isLoggedIn || currentUser == null) {
                System.out.println("❌ Please login first");
                return;
            }

            Long doctorId = currentUser.getId();

            System.out.println("🔄 Fetching your schedule...");

            // Call DoctorController to view schedule
            CommandResult result = doctorController.viewSchedule(doctorId);

            if (result.isSuccess()) {
                System.out.println("✅ " + result.getMessage());

                // Display appointments if available
                if (result.getData() instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Appointment> appointments = (List<Appointment>) result.getData();

                    if (appointments.isEmpty()) {
                        System.out.println("📋 No appointments scheduled");
                    } else {
                        System.out.println("\n📋 Your Schedule:");
                        System.out.println("─".repeat(80));
                        System.out.printf("%-4s %-12s %-12s %-20s %-15s%n",
                                "No.", "Date", "Time", "Patient", "Status");
                        System.out.println("─".repeat(80));

                        for (int i = 0; i < appointments.size(); i++) {
                            Appointment apt = appointments.get(i);
                            System.out.printf("%-4d %-12s %-12s %-20s %-15s%n",
                                    (i + 1),
                                    apt.getAppointmentDate() != null ? apt.getAppointmentDate().toString() : "N/A",
                                    apt.getAppointmentTime() != null ? apt.getAppointmentTime().toString() : "N/A",
                                    "Patient ID: " + apt.getPatientId(),
                                    apt.getStatus() != null ? apt.getStatus().toString() : "N/A");
                        }
                        System.out.println("─".repeat(80));
                    }
                }
            } else {
                System.out.println("❌ " + result.getMessage());
            }

        } catch (Exception e) {
            System.out.println("❌ View schedule error: " + e.getMessage());
        }
    }

    private void handleUpdateProfile() {
        System.out.println("\n📋 UPDATE PROFILE");
        System.out.println("=" .repeat(20));

        try {
            if (!isLoggedIn || currentUser == null) {
                System.out.println("❌ Please login first");
                return;
            }

            Long doctorId = currentUser.getId();

            System.out.println("📝 Update your profile information (leave blank to keep current value):");
            System.out.println();

            // Collect updated profile information
            String firstName = input.getString("👤 First Name: ");
            String lastName = input.getString("👤 Last Name: ");
            String email = input.getString("📧 Email: ");
            String phone = input.getString("📱 Phone Number: ");
            String specialization = input.getString("🩺 Specialization: ");

            // Validate email if provided
            if (!email.isEmpty() && !InputValidator.isValidEmail(email)) {
                System.out.println("❌ Invalid email format");
                return;
            }

            // Validate phone if provided
            if (!phone.isEmpty() && !InputValidator.isValidPhone(phone)) {
                System.out.println("❌ Invalid phone format");
                return;
            }

            System.out.println("\n🔄 Updating profile...");

            // Convert empty strings to null for the command
            String firstNameParam = firstName.isEmpty() ? null : firstName;
            String lastNameParam = lastName.isEmpty() ? null : lastName;
            String emailParam = email.isEmpty() ? null : email;
            String phoneParam = phone.isEmpty() ? null : phone;
            String specializationParam = specialization.isEmpty() ? null : specialization;

            // Call DoctorController to update profile
            CommandResult result = doctorController.updateProfile(doctorId,
                    firstNameParam, lastNameParam, emailParam, phoneParam, specializationParam);

            if (result.isSuccess()) {
                System.out.println("✅ " + result.getMessage());
                System.out.println("📝 Profile updated successfully!");
            } else {
                System.out.println("❌ " + result.getMessage());
            }

        } catch (Exception e) {
            System.out.println("❌ Update profile error: " + e.getMessage());
        }
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

    private void handleViewPatientRecords() {
        System.out.println("👤 VIEW PATIENT RECORDS - Coming soon!");
        System.out.println("📋 This feature will allow you to view patient medical records");
    }

    private void handleManageAvailability() {
        System.out.println("⏰ MANAGE AVAILABILITY - Coming soon!");
        System.out.println("📅 This feature will allow you to set your working hours and availability");
    }

    private void handleViewConsultationFees() {
        System.out.println("💰 VIEW CONSULTATION FEES - Coming soon!");
        System.out.println("💵 This feature will show your current consultation fee settings");
    }

    private void handleGenerateReports() {
        System.out.println("📊 GENERATE REPORTS - Coming soon!");
        System.out.println("📈 This feature will allow you to generate various medical reports");
    }

    private void handleAccountSettings() {
        System.out.println("🔧 ACCOUNT SETTINGS - Coming soon!");
        System.out.println("⚙️ This feature will allow you to manage your account settings");
    }
}
