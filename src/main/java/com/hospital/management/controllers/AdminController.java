package com.hospital.management.controllers;

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
}
