package com.hospital.management.ui.menus;

import com.hospital.management.controllers.AdminController;
import com.hospital.management.services.impl.UserServiceImpl;
import com.hospital.management.services.impl.AppointmentServiceImpl;
import com.hospital.management.services.impl.BillingServiceImpl;
import com.hospital.management.services.impl.PaymentServiceImpl;
import com.hospital.management.commands.AdminCommands.ViewReportsCommand.ReportType;
import com.hospital.management.commands.AdminCommands.ManageUsersCommand.UserManagementAction;
import com.hospital.management.ui.InputHandler;
import com.hospital.management.commands.CommandResult;
import com.hospital.management.models.User;
import com.hospital.management.common.enums.UserRole;

import java.util.Optional;
import java.util.List;
import java.util.Map;

/**
 * Admin Menu UI with login/logout functionality and proper controller integration
 */
public class AdminMenuUI {
    private final AdminController adminController;
    private final UserServiceImpl userService;
    private final InputHandler input;

    // Session management
    private User currentUser = null;
    private boolean isLoggedIn = false;

    public AdminMenuUI() {
        this.userService = new UserServiceImpl();
        this.adminController = new AdminController(
                userService,
                new AppointmentServiceImpl(),
                new BillingServiceImpl(),
                new PaymentServiceImpl()
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
                // Show admin dashboard
                if (!showAdminDashboard()) {
                    return; // Logout and back to main menu
                }
            }
        }
    }

    private boolean showLoginMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("🔐 ADMIN PORTAL");
        System.out.println("=".repeat(50));
        System.out.println("⚠️  Restricted Access - Admin Login Required");
        System.out.println();
        System.out.println("1. 🔑 Admin Login");
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

    private boolean showAdminDashboard() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🔐 ADMIN DASHBOARD - Welcome " + currentUser.getUsername());
        System.out.println("=".repeat(60));
        System.out.println("👥 USER MANAGEMENT:");
        System.out.println("  1. 👤 View All Users");
        System.out.println("  2. 🔍 Search User Details");
        System.out.println("  3. 🗑️  Delete User");
        System.out.println();
        System.out.println("📊 REPORTS & ANALYTICS:");
        System.out.println("  4. 📈 Dashboard Summary");
        System.out.println("  5. 📅 Appointment Reports");
        System.out.println("  6. 💰 Financial Reports");
        System.out.println("  7. 👥 User Statistics");
        System.out.println();
        System.out.println("🏥 HOSPITAL MANAGEMENT:");
        System.out.println("  8. 🏢 Manage Departments");
        System.out.println("  9. ⚙️  System Settings");
        System.out.println("  0. 🚪 Logout");
        System.out.println("=".repeat(60));

        int choice = input.getInt("Select an option (0-9): ", 0, 9);

        switch (choice) {
            case 1 -> handleViewAllUsers();
            case 2 -> handleSearchUserDetails();
            case 3 -> handleDeleteUser();
            case 4 -> handleDashboardSummary();
            case 5 -> handleAppointmentReports();
            case 6 -> handleFinancialReports();
            case 7 -> handleUserStatistics();
            case 8 -> handleManageDepartments();
            case 9 -> handleSystemSettings();
            case 0 -> {
                handleLogout();
                return false;
            }
            default -> System.out.println("❌ Invalid option.");
        }
        return true;
    }

    private void handleLogin() {
        System.out.println("\n🔑 ADMIN LOGIN");
        System.out.println("=" .repeat(20));

        try {
            String loginId = input.getString("👤 Admin Username: ");
            String password = input.getString("🔑 Admin Password: ");

            if (loginId.isEmpty() || password.isEmpty()) {
                System.out.println("❌ Admin credentials are required");
                return;
            }

            System.out.println("\n🔄 Verifying admin access...");

            // Authenticate using UserService
            boolean authenticated = userService.authenticate(loginId, password);

            if (authenticated) {
                // Get user details and verify admin role
                Optional<User> userOpt = userService.findUserByUsername(loginId);
                if (userOpt.isEmpty()) {
                    // Try finding by email if username didn't work
                    userOpt = userService.findAllUsers().stream()
                            .filter(u -> u.getEmail().equals(loginId))
                            .findFirst();
                }

                if (userOpt.isPresent() && userOpt.get().getRole() == UserRole.ADMIN) {
                    currentUser = userOpt.get();
                    isLoggedIn = true;
                    System.out.println("✅ Admin authentication successful!");
                    System.out.println("🔐 Welcome Admin " + currentUser.getUsername() + "!");
                } else {
                    System.out.println("❌ Access denied: Not an admin account");
                    System.out.println("⚠️  Unauthorized access attempt logged");
                }
            } else {
                System.out.println("❌ Admin login failed: Invalid credentials");
                System.out.println("⚠️  Unauthorized access attempt logged");
            }

        } catch (Exception e) {
            System.out.println("❌ Authentication error: " + e.getMessage());
        }
    }

    private void handleLogout() {
        System.out.println("\n🔄 Logging out...");
        System.out.println("👋 Goodbye, Admin " + (currentUser != null ? currentUser.getUsername() : "User") + "!");
        currentUser = null;
        isLoggedIn = false;
        System.out.println("✅ Admin session ended successfully!");
    }

    private void handleViewAllUsers() {
        System.out.println("\n👥 VIEW ALL USERS");
        System.out.println("=" .repeat(20));

        try {
            if (!isLoggedIn || currentUser == null) {
                System.out.println("❌ Please login first");
                return;
            }

            Long adminId = currentUser.getId();

            System.out.println("🔄 Fetching all users...");

            // Call AdminController to view all users
            CommandResult result = adminController.viewAllUsers(adminId);

            if (result.isSuccess()) {
                System.out.println("✅ " + result.getMessage());

                if (result.getData() instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<User> users = (List<User>) result.getData();

                    if (users.isEmpty()) {
                        System.out.println("📋 No users found");
                    } else {
                        System.out.println("\n👥 System Users:");
                        System.out.println("─".repeat(80));
                        System.out.printf("%-4s %-15s %-25s %-15s %-10s%n",
                                "ID", "Username", "Email", "Phone", "Role");
                        System.out.println("─".repeat(80));

                        for (User user : users) {
                            System.out.printf("%-4s %-15s %-25s %-15s %-10s%n",
                                    user.getId() != null ? user.getId() : "N/A",
                                    user.getUsername() != null ? user.getUsername() : "N/A",
                                    user.getEmail() != null ? user.getEmail() : "N/A",
                                    user.getPhone() != null ? user.getPhone() : "N/A",
                                    user.getRole() != null ? user.getRole() : "N/A");
                        }
                        System.out.println("─".repeat(80));
                        System.out.println("Total Users: " + users.size());
                    }
                }
            } else {
                System.out.println("❌ " + result.getMessage());
            }

        } catch (Exception e) {
            System.out.println("❌ View users error: " + e.getMessage());
        }
    }

    private void handleSearchUserDetails() {
        System.out.println("\n🔍 SEARCH USER DETAILS");
        System.out.println("=" .repeat(25));

        try {
            if (!isLoggedIn || currentUser == null) {
                System.out.println("❌ Please login first");
                return;
            }

            Long adminId = currentUser.getId();
            Long targetUserId = Long.valueOf(input.getInt("👤 Enter User ID: ", 1, 999999));

            System.out.println("🔄 Fetching user details...");

            // Call AdminController to get user details
            CommandResult result = adminController.getUserDetails(adminId, targetUserId);

            if (result.isSuccess()) {
                System.out.println("✅ " + result.getMessage());

                if (result.getData() instanceof User) {
                    User user = (User) result.getData();
                    System.out.println("\n👤 User Details:");
                    System.out.println("─".repeat(40));
                    System.out.println("ID: " + user.getId());
                    System.out.println("Username: " + user.getUsername());
                    System.out.println("Email: " + user.getEmail());
                    System.out.println("Phone: " + user.getPhone());
                    System.out.println("Role: " + user.getRole());
                    System.out.println("Active: " + (user.isActive() ? "Yes" : "No"));
                    System.out.println("─".repeat(40));
                }
            } else {
                System.out.println("❌ " + result.getMessage());
            }

        } catch (Exception e) {
            System.out.println("❌ Search user error: " + e.getMessage());
        }
    }

    private void handleDeleteUser() {
        System.out.println("\n🗑️ DELETE USER");
        System.out.println("=" .repeat(15));
        System.out.println("⚠️  WARNING: This action cannot be undone!");

        try {
            if (!isLoggedIn || currentUser == null) {
                System.out.println("❌ Please login first");
                return;
            }

            Long adminId = currentUser.getId();
            Long targetUserId = Long.valueOf(input.getInt("👤 Enter User ID to delete: ", 1, 999999));

            // Confirm deletion
            String confirmation = input.getString("Type 'DELETE' to confirm: ");
            if (!"DELETE".equals(confirmation)) {
                System.out.println("❌ Operation cancelled");
                return;
            }

            System.out.println("🔄 Deleting user...");

            // Call AdminController to delete user
            CommandResult result = adminController.deleteUser(adminId, targetUserId);

            if (result.isSuccess()) {
                System.out.println("✅ " + result.getMessage());
                System.out.println("🗑️ User deleted successfully");
            } else {
                System.out.println("❌ " + result.getMessage());
            }

        } catch (Exception e) {
            System.out.println("❌ Delete user error: " + e.getMessage());
        }
    }

    private void handleDashboardSummary() {
        System.out.println("\n📈 DASHBOARD SUMMARY");
        System.out.println("=" .repeat(25));

        try {
            if (!isLoggedIn || currentUser == null) {
                System.out.println("❌ Please login first");
                return;
            }

            Long adminId = currentUser.getId();

            System.out.println("🔄 Generating dashboard summary...");

            // Call AdminController to generate dashboard summary
            CommandResult result = adminController.generateDashboardSummary(adminId);

            if (result.isSuccess()) {
                System.out.println("✅ " + result.getMessage());

                if (result.getData() instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> summary = (Map<String, Object>) result.getData();

                    System.out.println("\n📊 Hospital Management System Summary:");
                    System.out.println("═".repeat(50));
                    System.out.println("👥 Total Users: " + summary.get("totalUsers"));
                    System.out.println("📅 Total Appointments: " + summary.get("totalAppointments"));
                    System.out.println("💰 Total Bills: " + summary.get("totalBills"));
                    System.out.println("💳 Total Payments: " + summary.get("totalPayments"));
                    System.out.println("🕒 Generated At: " + summary.get("generatedAt"));
                    System.out.println("═".repeat(50));
                }
            } else {
                System.out.println("❌ " + result.getMessage());
            }

        } catch (Exception e) {
            System.out.println("❌ Dashboard summary error: " + e.getMessage());
        }
    }

    private void handleAppointmentReports() {
        System.out.println("\n📅 APPOINTMENT REPORTS");
        System.out.println("=" .repeat(25));

        try {
            if (!isLoggedIn || currentUser == null) {
                System.out.println("❌ Please login first");
                return;
            }

            Long adminId = currentUser.getId();

            System.out.println("🔄 Generating appointment report...");

            // Call AdminController to generate appointment report
            CommandResult result = adminController.generateReport(adminId, ReportType.APPOINTMENT_REPORT);

            if (result.isSuccess()) {
                System.out.println("✅ " + result.getMessage());
                System.out.println("📅 Appointment report generated successfully");
            } else {
                System.out.println("❌ " + result.getMessage());
            }

        } catch (Exception e) {
            System.out.println("❌ Appointment report error: " + e.getMessage());
        }
    }

    private void handleFinancialReports() {
        System.out.println("\n💰 FINANCIAL REPORTS");
        System.out.println("=" .repeat(22));

        try {
            if (!isLoggedIn || currentUser == null) {
                System.out.println("❌ Please login first");
                return;
            }

            Long adminId = currentUser.getId();

            System.out.println("🔄 Generating financial report...");

            // Call AdminController to generate financial report
            CommandResult result = adminController.generateReport(adminId, ReportType.FINANCIAL_REPORT);

            if (result.isSuccess()) {
                System.out.println("✅ " + result.getMessage());
                System.out.println("💰 Financial report generated successfully");
            } else {
                System.out.println("❌ " + result.getMessage());
            }

        } catch (Exception e) {
            System.out.println("❌ Financial report error: " + e.getMessage());
        }
    }

    private void handleUserStatistics() {
        System.out.println("\n👥 USER STATISTICS");
        System.out.println("=" .repeat(20));

        try {
            if (!isLoggedIn || currentUser == null) {
                System.out.println("❌ Please login first");
                return;
            }

            Long adminId = currentUser.getId();

            System.out.println("🔄 Generating user statistics...");

            // Call AdminController to generate user statistics
            CommandResult result = adminController.generateReport(adminId, ReportType.USER_STATISTICS);

            if (result.isSuccess()) {
                System.out.println("✅ " + result.getMessage());
                System.out.println("👥 User statistics generated successfully");
            } else {
                System.out.println("❌ " + result.getMessage());
            }

        } catch (Exception e) {
            System.out.println("❌ User statistics error: " + e.getMessage());
        }
    }

    // Placeholder methods for future implementation
    private void handleForgotPassword() {
        System.out.println("\n🔑 ADMIN PASSWORD RECOVERY");
        System.out.println("🔐 For security reasons, admin password recovery requires manual verification");
        System.out.println("📧 Please contact the system administrator or Team16 for password reset");
        System.out.println("🚧 Automated admin password recovery - Coming soon!");
    }

    private void handleManageDepartments() {
        System.out.println("🏢 MANAGE DEPARTMENTS - Coming soon!");
        System.out.println("🏥 This feature will allow you to manage hospital departments");
    }

    private void handleSystemSettings() {
        System.out.println("⚙️ SYSTEM SETTINGS - Coming soon!");
        System.out.println("🔧 This feature will allow you to configure system-wide settings");
    }
}
