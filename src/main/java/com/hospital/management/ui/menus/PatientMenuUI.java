package com.hospital.management.ui.menus;

import com.hospital.management.controllers.PatientController;
import com.hospital.management.services.impl.UserServiceImpl;
import com.hospital.management.ui.InputHandler;
import com.hospital.management.commands.CommandResult;
import com.hospital.management.models.Patient;
import com.hospital.management.models.User;
import com.hospital.management.common.utils.InputValidator;
import com.hospital.management.common.enums.UserRole;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;

/**
 * Patient Menu UI with login/logout functionality
 */
public class PatientMenuUI {
    private final PatientController patientController;
    private final UserServiceImpl userService;
    private final InputHandler input;

    // Session management
    private User currentUser = null;
    private boolean isLoggedIn = false;

    public PatientMenuUI(PatientController patientController) {
        this.patientController = patientController;
        this.userService = new UserServiceImpl();
        this.input = InputHandler.getInstance();
    }

    public void show() {
        while (true) {
            if (!isLoggedIn) {
                // Show login/register menu
                if (!showLoginMenu()) {
                    return; // Back to main menu
                }
            } else {
                // Show patient dashboard
                if (!showPatientDashboard()) {
                    return; // Logout and back to main menu
                }
            }
        }
    }

    private boolean showLoginMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("👤 PATIENT PORTAL");
        System.out.println("=".repeat(50));
        System.out.println("1. 📝 Register New Account");
        System.out.println("2. 🔑 Login to Existing Account");
        System.out.println("3. ❓ Forgot Password");
        System.out.println("0. ⬅️  Back to Main Menu");
        System.out.println("=".repeat(50));

        int choice = input.getInt("Select an option (0-3): ", 0, 3);

        switch (choice) {
            case 1 -> handleRegistration();
            case 2 -> handleLogin();
            case 3 -> handleForgotPassword();
            case 0 -> { return false; }
            default -> System.out.println("❌ Invalid option.");
        }
        return true;
    }

    private boolean showPatientDashboard() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("👤 PATIENT DASHBOARD - Welcome " + currentUser.getUsername() + "!");
        System.out.println("=".repeat(50));
        System.out.println("1. 📅 Book Appointment");
        System.out.println("2. 👁️  View My Appointments");
        System.out.println("3. 📋 Update Profile");
        System.out.println("4. 📊 View Medical History");
        System.out.println("5. 💰 View Bills & Payments");
        System.out.println("9. 🔧 Account Settings");
        System.out.println("0. 🚪 Logout");
        System.out.println("=".repeat(50));

        int choice = input.getInt("Select an option (0-9): ", 0, 9);

        switch (choice) {
            case 1 -> handleBookAppointment();
            case 2 -> handleViewAppointments();
            case 3 -> handleUpdateProfile();
            case 4 -> handleViewMedicalHistory();
            case 5 -> handleViewBills();
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
        System.out.println("\n🔑 PATIENT LOGIN");
        System.out.println("=" .repeat(20));

        try {
            String loginId = input.getString("👤 Username: ");
            String password = input.getPasswordInput("🔑 Password: ");

            if (loginId.isEmpty() || password.isEmpty()) {
                System.out.println("❌ Username and password are required");
                return;
            }

            System.out.println("\n🔄 Authenticating...");

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

                if (userOpt.isPresent() && userOpt.get().getRole() == UserRole.PATIENT) {
                    currentUser = userOpt.get();
                    isLoggedIn = true;
                    System.out.println("✅ Login successful!");
                    System.out.println("👋 Welcome back, " + currentUser.getUsername() + "!");
                } else {
                    System.out.println("❌ Access denied: Not a patient account");
                }
            } else {
                System.out.println("❌ Login failed: Invalid credentials");
                System.out.println("💡 Please check your username/email and password");
            }

        } catch (Exception e) {
            System.out.println("❌ Login error: " + e.getMessage());
        }
    }

    private void handleLogout() {
        System.out.println("\n🔄 Logging out...");
        System.out.println("👋 Goodbye, " + (currentUser != null ? currentUser.getUsername() : "User") + "!");
        currentUser = null;
        isLoggedIn = false;
        System.out.println("✅ Logged out successfully!");
    }

    private void handleRegistration() {
        System.out.println("\n📝 PATIENT REGISTRATION");
        System.out.println("=" .repeat(30));

        try {
            // Collect basic required information
            String firstName = getValidatedInput("👤 First Name: ", InputValidator::isValidName);
            String lastName = getValidatedInput("👤 Last Name: ", InputValidator::isValidName);
            String email = getValidatedInput("📧 Email: ", InputValidator::isValidEmail);
            String phone = getValidatedInput("📱 Phone Number: ", InputValidator::isValidPhone);
            String username = getValidatedInput("👤 Username: ", InputValidator::isValidUsername);


            String password = input.getPasswordInput("🔑Password: ");
            String confirmPassword = getValidatedPassword("🔑Confirm Password: ");

            if (!password.equals(confirmPassword)) {
                System.out.println("❌ Passwords do not match");
                return;
            }

            // Get date of birth
            LocalDate dateOfBirth = getDateInput("📅 Date of Birth (YYYY-MM-DD): ");

            // Get gender
            Patient.Gender gender = getGenderInput();

            // Ask if user wants to provide additional info
            System.out.print("📋 Would you like to provide additional information? (y/n): ");
            String addInfo = input.getString("").toLowerCase();

            CommandResult result;

            if (addInfo.equals("y") || addInfo.equals("yes")) {
                // Collect additional information
                String bloodGroup = input.getString("🩸 Blood Group (optional): ");
                if (bloodGroup.trim().isEmpty()) bloodGroup = null;

                String address = input.getString("🏠 Address (optional): ");
                if (address.trim().isEmpty()) address = null;

                String emergencyContactName = input.getString("👤 Emergency Contact Name (optional): ");
                if (emergencyContactName.trim().isEmpty()) emergencyContactName = null;

                String emergencyContactPhone = input.getString("📞 Emergency Contact Phone (optional): ");
                if (emergencyContactPhone.trim().isEmpty()) emergencyContactPhone = null;

                System.out.println("\n🔄 Processing full registration...");

                // Call PatientController with full registration
                result = patientController.registerPatientFull(username, password, email, phone,
                        firstName, lastName, dateOfBirth, gender,
                        bloodGroup, address, emergencyContactName, emergencyContactPhone);
            } else {
                System.out.println("\n🔄 Processing basic registration...");

                // Call PatientController with basic registration
                result = patientController.registerPatient(username, password, email, phone,
                        firstName, lastName, dateOfBirth, gender);
            }

            // Handle result
            if (result.isSuccess()) {
                System.out.println("✅ " + result.getMessage());
                System.out.println("📧 Account created for: " + firstName + " " + lastName);
                System.out.println("👤 Username: " + username);
                System.out.println("💡 You can now login with your credentials");
            } else {
                System.out.println("❌ " + result.getMessage());
            }

        } catch (Exception e) {
            System.out.println("❌ Registration error: " + e.getMessage());
        }
    }

    private void handleBookAppointment() {
        System.out.println("\n📅 BOOK APPOINTMENT");
        System.out.println("=" .repeat(25));

        try {
            if (!isLoggedIn || currentUser == null) {
                System.out.println("❌ Please login first");
                return;
            }

            Long patientId = currentUser.getId();

            // Get doctor ID (simplified - in real app, you'd show available doctors)
            System.out.println("\n👨‍⚕️ Available Doctors:");
            System.out.println("1. Dr. John Smith (ID: 1) - Cardiology");
            System.out.println("2. Dr. Jane Doe (ID: 2) - Neurology");
            System.out.println("3. Dr. Bob Wilson (ID: 3) - Orthopedics");

            Long doctorId = Long.valueOf(input.getInt("Select doctor ID (1-3): ", 1, 3));

            // Get appointment date
            LocalDate appointmentDate = getDateInput("📅 Appointment Date (YYYY-MM-DD): ");

            // Get appointment time
            LocalTime appointmentTime = getTimeInput();

            // Get reason (optional)
            String reason = input.getString("📝 Reason for appointment (optional): ");
            if (reason.trim().isEmpty()) reason = null;

            System.out.println("\n🔄 Booking appointment...");

            // Call PatientController to book appointment
            CommandResult result = patientController.bookAppointment(patientId, doctorId,
                    appointmentDate, appointmentTime, reason);

            // Handle result
            if (result.isSuccess()) {
                System.out.println("✅ " + result.getMessage());
                System.out.println("📅 Appointment scheduled for " + appointmentDate + " at " + appointmentTime);
            } else {
                System.out.println("❌ " + result.getMessage());
            }

        } catch (Exception e) {
            System.out.println("❌ Booking error: " + e.getMessage());
        }
    }

    private void handleViewAppointments() {
        System.out.println("\n👁️ VIEW APPOINTMENTS");
        System.out.println("=" .repeat(20));

        try {
            if (!isLoggedIn || currentUser == null) {
                System.out.println("❌ Please login first");
                return;
            }

            Long patientId = currentUser.getId();

            System.out.println("\n🔄 Fetching your appointments...");

            // Call PatientController to view appointments
            CommandResult result = patientController.viewAppointments(patientId);

            // Handle result
            if (result.isSuccess()) {
                System.out.println("✅ " + result.getMessage());

                // Display appointments if available in result data
                if (result.getData() != null) {
                    System.out.println("📋 Your appointments have been retrieved successfully");
                    // Additional display logic can be added here based on result data
                } else {
                    System.out.println("📋 No appointments found");
                }
            } else {
                System.out.println("❌ " + result.getMessage());
            }

        } catch (Exception e) {
            System.out.println("❌ View appointments error: " + e.getMessage());
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

    private void handleUpdateProfile() {
        System.out.println("📋 UPDATE PROFILE - Coming soon!");
    }

    private void handleViewMedicalHistory() {
        System.out.println("📊 MEDICAL HISTORY - Coming soon!");
    }

    private void handleViewBills() {
        System.out.println("💰 BILLS & PAYMENTS - Coming soon!");
    }

    private void handleAccountSettings() {
        System.out.println("🔧 ACCOUNT SETTINGS - Coming soon!");
    }

    // Helper methods for input validation (same as before)
    private String getValidatedInput(String prompt, java.util.function.Predicate<String> validator) {
        while (true) {
            String input = this.input.getString(prompt);
            if (validator.test(input)) {
                return input;
            }
            System.out.println("❌ Invalid input format. Please try again.");
        }
    }

    private String getValidatedPassword(String prompt) {
        while (true) {
            String password = input.getString(prompt);
            if (InputValidator.isValidPassword(password)) {
                return password;
            }
            System.out.println("❌ Password must be at least 8 characters with uppercase, lowercase, digit, and special character.");
        }
    }

    private LocalDate getDateInput(String prompt) {
        while (true) {
            try {
                String dateStr = input.getString(prompt);
                return LocalDate.parse(dateStr);
            } catch (DateTimeParseException e) {
                System.out.println("❌ Invalid date format. Please use YYYY-MM-DD format.");
            }
        }
    }

    private Patient.Gender getGenderInput() {
        while (true) {
            try {
                System.out.println("⚧ Gender options: MALE, FEMALE, OTHER");
                String genderStr = input.getString("Enter gender: ").toUpperCase();
                return Patient.Gender.valueOf(genderStr);
            } catch (IllegalArgumentException e) {
                System.out.println("❌ Invalid gender. Please enter MALE, FEMALE, or OTHER.");
            }
        }
    }

    private LocalTime getTimeInput() {
        System.out.println("\n⏰ Available Time Slots:");
        System.out.println("1. 09:00 AM");
        System.out.println("2. 10:00 AM");
        System.out.println("3. 11:00 AM");
        System.out.println("4. 02:00 PM");
        System.out.println("5. 03:00 PM");
        System.out.println("6. 04:00 PM");

        int timeChoice = input.getInt("Select time slot (1-6): ", 1, 6);

        return switch (timeChoice) {
            case 1 -> LocalTime.of(9, 0);
            case 2 -> LocalTime.of(10, 0);
            case 3 -> LocalTime.of(11, 0);
            case 4 -> LocalTime.of(14, 0);
            case 5 -> LocalTime.of(15, 0);
            case 6 -> LocalTime.of(16, 0);
            default -> LocalTime.of(10, 0);
        };
    }
}
