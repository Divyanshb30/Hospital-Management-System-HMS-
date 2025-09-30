package com.hospital.management.commands.AdminCommands;

import com.hospital.management.commands.Command;
import com.hospital.management.commands.CommandResult;
import com.hospital.management.common.exceptions.DatabaseException;
import com.hospital.management.common.exceptions.ValidationException;
import com.hospital.management.common.exceptions.BusinessLogicException;
import com.hospital.management.interfaces.AppointmentService;
import com.hospital.management.interfaces.BillingService;
import com.hospital.management.interfaces.PaymentService;
import com.hospital.management.interfaces.UserService;
import com.hospital.management.models.Appointment;
import com.hospital.management.models.Bill;
import com.hospital.management.models.Payment;
import com.hospital.management.models.User;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Command to generate system reports for admin dashboard
 * Uses multiple services to gather report data
 */
public class ViewReportsCommand implements Command {

    public enum ReportType {
        DASHBOARD_SUMMARY, APPOINTMENT_REPORT, FINANCIAL_REPORT, USER_STATISTICS
    }

    private final Long adminId;
    private final ReportType reportType;

    // Service dependencies
    private final AppointmentService appointmentService;
    private final BillingService billingService;
    private final PaymentService paymentService;
    private final UserService userService;

    public ViewReportsCommand(Long adminId, ReportType reportType,
                              AppointmentService appointmentService, BillingService billingService,
                              PaymentService paymentService, UserService userService) {
        this.adminId = adminId;
        this.reportType = reportType;
        this.appointmentService = appointmentService;
        this.billingService = billingService;
        this.paymentService = paymentService;
        this.userService = userService;
    }

    // Constructor for dashboard summary (default)
    public ViewReportsCommand(Long adminId, AppointmentService appointmentService,
                              BillingService billingService, PaymentService paymentService,
                              UserService userService) {
        this.adminId = adminId;
        this.reportType = ReportType.DASHBOARD_SUMMARY;
        this.appointmentService = appointmentService;
        this.billingService = billingService;
        this.paymentService = paymentService;
        this.userService = userService;
    }

    @Override
    public CommandResult execute() throws DatabaseException, ValidationException, BusinessLogicException {
        try {
            // Validate parameters first
            if (!validateParameters()) {
                return CommandResult.failure("Invalid parameters for report generation");
            }

            // Generate report based on type
            switch (reportType) {
                case DASHBOARD_SUMMARY:
                    return generateDashboardSummary();

                case APPOINTMENT_REPORT:
                    return generateAppointmentReport();

                case FINANCIAL_REPORT:
                    return generateFinancialReport();

                case USER_STATISTICS:
                    return generateUserStatistics();

                default:
                    throw new BusinessLogicException("Unsupported report type: " + reportType);
            }

        } catch (ValidationException | BusinessLogicException e) {
            return CommandResult.failure("Report generation failed: " + e.getMessage(), e);
        } catch (Exception e) {
            return CommandResult.failure("Unexpected error during report generation: " + e.getMessage(), e);
        }
    }

    @Override
    public String getDescription() {
        return "Generate " + reportType + " for admin ID " + adminId;
    }

    @Override
    public boolean validateParameters() throws ValidationException {
        // Validate required dependencies
        if (appointmentService == null || billingService == null ||
                paymentService == null || userService == null) {
            throw new ValidationException("All service dependencies are required", "Services");
        }

        if (adminId == null || adminId <= 0) {
            throw new ValidationException("Valid admin ID is required", "AdminId", adminId);
        }

        if (reportType == null) {
            throw new ValidationException("Report type is required", "ReportType");
        }

        return true;
    }

    // Report generation methods

    private CommandResult generateDashboardSummary() {
        Map<String, Object> summary = new HashMap<>();

        try {
            // Get counts from services
            List<User> allUsers = userService.findAllUsers();
            List<Appointment> allAppointments = appointmentService.getAllAppointments();
            List<Bill> allBills = billingService.getAllBills();
            List<Payment> allPayments = paymentService.getAllPayments();

            summary.put("totalUsers", allUsers.size());
            summary.put("totalAppointments", allAppointments.size());
            summary.put("totalBills", allBills.size());
            summary.put("totalPayments", allPayments.size());
            summary.put("generatedAt", LocalDateTime.now());

            return CommandResult.success("Dashboard summary generated successfully", summary);

        } catch (Exception e) {
            return CommandResult.failure("Failed to generate dashboard summary: " + e.getMessage(), e);
        }
    }

    private CommandResult generateAppointmentReport() {
        Map<String, Object> report = new HashMap<>();

        try {
            List<Appointment> appointments = appointmentService.getAllAppointments();

            report.put("totalAppointments", appointments.size());
            report.put("appointments", appointments);
            report.put("generatedAt", LocalDateTime.now());

            return CommandResult.success("Appointment report generated successfully", report);

        } catch (Exception e) {
            return CommandResult.failure("Failed to generate appointment report: " + e.getMessage(), e);
        }
    }

    private CommandResult generateFinancialReport() {
        Map<String, Object> report = new HashMap<>();

        try {
            List<Bill> bills = billingService.getAllBills();
            List<Payment> payments = paymentService.getAllPayments();

            report.put("totalBills", bills.size());
            report.put("totalPayments", payments.size());
            report.put("bills", bills);
            report.put("payments", payments);
            report.put("generatedAt", LocalDateTime.now());

            return CommandResult.success("Financial report generated successfully", report);

        } catch (Exception e) {
            return CommandResult.failure("Failed to generate financial report: " + e.getMessage(), e);
        }
    }

    private CommandResult generateUserStatistics() {
        Map<String, Object> stats = new HashMap<>();

        try {
            List<User> users = userService.findAllUsers();

            stats.put("totalUsers", users.size());
            stats.put("users", users);
            stats.put("generatedAt", LocalDateTime.now());

            return CommandResult.success("User statistics generated successfully", stats);

        } catch (Exception e) {
            return CommandResult.failure("Failed to generate user statistics: " + e.getMessage(), e);
        }
    }
}
