package com.hospital.management.controllers;
import com.hospital.management.commands.AdminCommands.ViewAllAppointmentsCommand;
import com.hospital.management.commands.AdminCommands.ManageDepartmentsCommand;
import com.hospital.management.commands.AdminCommands.AddDoctorCommand;
import com.hospital.management.commands.AdminCommands.ViewAdminProfileCommand;
import java.math.BigDecimal;
import java.util.Optional;


import com.hospital.management.commands.Command;
import com.hospital.management.commands.CommandResult;
import com.hospital.management.commands.AdminCommands.ManageUsersCommand;
import com.hospital.management.commands.AdminCommands.ViewReportsCommand;
import com.hospital.management.commands.AdminCommands.ManageUsersCommand.UserManagementAction;
import com.hospital.management.commands.AdminCommands.ViewReportsCommand.ReportType;
import com.hospital.management.interfaces.UserService;
import com.hospital.management.interfaces.AppointmentService;
import com.hospital.management.interfaces.BillingService;
import com.hospital.management.interfaces.PaymentService;
import com.hospital.management.models.Department;
import com.hospital.management.models.User;

public class AdminController {

    private final UserService userService;
    private final AppointmentService appointmentService;
    private final BillingService billingService;
    private final PaymentService paymentService;

    public AdminController(UserService userService,
                           AppointmentService appointmentService,
                           BillingService billingService,
                           PaymentService paymentService) {
        this.userService = userService;
        this.appointmentService = appointmentService;
        this.billingService = billingService;
        this.paymentService = paymentService;
    }

    // View all users
    public CommandResult viewAllUsers(Long adminId) {
        Command command = new ManageUsersCommand(adminId, userService);
        try {
            return command.execute();
        } catch (Exception e) {
            return CommandResult.failure("Error viewing users: " + e.getMessage(), e);
        }
    }

    // Get user details by ID
    public CommandResult getUserDetails(Long adminId, Long targetUserId) {
        Command command = new ManageUsersCommand(adminId, UserManagementAction.GET_USER_DETAILS, targetUserId, userService);
        try {
            return command.execute();
        } catch (Exception e) {
            return CommandResult.failure("Error getting user details: " + e.getMessage(), e);
        }
    }

    // Delete a user by admin
    public CommandResult deleteUser(Long adminId, Long targetUserId) {
        Command command = new ManageUsersCommand(adminId, UserManagementAction.DELETE_USER, targetUserId, userService);
        try {
            return command.execute();
        } catch (Exception e) {
            return CommandResult.failure("Error deleting user: " + e.getMessage(), e);
        }
    }

    // Generate reports with all required services and report type
    public CommandResult generateReport(Long adminId, ReportType reportType) {
        Command command = new ViewReportsCommand(
                adminId,
                reportType,
                appointmentService,
                billingService,
                paymentService,
                userService
        );
        try {
            return command.execute();
        } catch (Exception e) {
            return CommandResult.failure("Error generating report: " + e.getMessage(), e);
        }
    }

    // Convenience method for dashboard summary report (no reportType needed)
    public CommandResult generateDashboardSummary(Long adminId) {
        Command command = new ViewReportsCommand(
                adminId,
                appointmentService,
                billingService,
                paymentService,
                userService
        );
        try {
            return command.execute();
        } catch (Exception e) {
            return CommandResult.failure("Error generating dashboard summary: " + e.getMessage(), e);
        }
    }

    public CommandResult viewAllAppointments(Long adminId) {
        Command command = new ViewAllAppointmentsCommand(adminId);
        try {
            return command.execute();
        } catch (Exception e) {
            return CommandResult.failure("Error viewing appointments: " + e.getMessage(), e);
        }
    }

    public CommandResult viewAllDepartments(Long adminId) {
        Command command = new ManageDepartmentsCommand(adminId, ManageDepartmentsCommand.DepartmentAction.VIEW_ALL);
        try {
            return command.execute();
        } catch (Exception e) {
            return CommandResult.failure("Error viewing departments: " + e.getMessage(), e);
        }
    }

    public CommandResult addDepartment(Long adminId, String name, String description, String location, String phone) {
        Department department = new Department(name, description, location);
        department.setPhone(phone);

        Command command = new ManageDepartmentsCommand(adminId, ManageDepartmentsCommand.DepartmentAction.ADD_DEPARTMENT, department);
        try {
            return command.execute();
        } catch (Exception e) {
            return CommandResult.failure("Error adding department: " + e.getMessage(), e);
        }
    }

    public CommandResult deleteDepartment(Long adminId, Long departmentId) {
        Command command = new ManageDepartmentsCommand(adminId, ManageDepartmentsCommand.DepartmentAction.DELETE_DEPARTMENT, departmentId);
        try {
            return command.execute();
        } catch (Exception e) {
            return CommandResult.failure("Error deleting department: " + e.getMessage(), e);
        }
    }

    public CommandResult addDoctor(Long adminId, String username, String password, String email, String phone,
                                   String firstName, String lastName, String specialization, String licenseNumber,
                                   Long departmentId, String qualification, int experienceYears, BigDecimal consultationFee) {
        Command command = new AddDoctorCommand(adminId, username, password, email, phone, firstName, lastName,
                specialization, licenseNumber, departmentId, qualification,
                experienceYears, consultationFee, userService);
        try {
            return command.execute();
        } catch (Exception e) {
            return CommandResult.failure("Error adding doctor: " + e.getMessage(), e);
        }
    }

    public CommandResult viewAdminProfile(Long adminId) {
        Command command = new ViewAdminProfileCommand(adminId, userService);
        try {
            return command.execute();
        } catch (Exception e) {
            return CommandResult.failure("Error viewing admin profile: " + e.getMessage(), e);
        }
    }

    public CommandResult changeAdminPassword(Long adminId, String oldPassword, String newPassword) {
        // This would require a new command, but for now can use UserService directly
        try {
            Optional<User> userOpt = userService.findUserById(adminId);
            if (userOpt.isEmpty()) {
                return CommandResult.failure("Admin not found", null);
            }

            User admin = userOpt.get();
            boolean authenticated = userService.authenticate(admin.getUsername(), oldPassword);
            if (!authenticated) {
                return CommandResult.failure("Current password is incorrect", null);
            }

            // Update password (this would need to be implemented in UserService)
            boolean updated = userService.updateUserPassword(adminId, newPassword);
            if (updated) {
                return CommandResult.success("Password updated successfully", null);
            } else {
                return CommandResult.failure("Failed to update password", null);
            }

        } catch (Exception e) {
            return CommandResult.failure("Error changing password: " + e.getMessage(), e);
        }
    }
}
