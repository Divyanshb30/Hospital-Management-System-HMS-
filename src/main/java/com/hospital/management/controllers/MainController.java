package com.hospital.management.controllers;

import com.hospital.management.commands.CommandResult;
import com.hospital.management.controllers.PatientController;
import com.hospital.management.controllers.DoctorController;
import com.hospital.management.controllers.AdminController;
import com.hospital.management.interfaces.UserService;
import com.hospital.management.interfaces.AppointmentService;
import com.hospital.management.interfaces.BillingService;
import com.hospital.management.interfaces.PaymentService;
import com.hospital.management.models.Patient;
import com.hospital.management.commands.AdminCommands.ViewReportsCommand.ReportType;
import com.hospital.management.commands.AdminCommands.ManageUsersCommand.UserManagementAction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class MainController {

    private final PatientController patientController;
    private final DoctorController doctorController;
    private final AdminController adminController;

    public MainController(UserService userService, AppointmentService appointmentService,
                          BillingService billingService, PaymentService paymentService) {
        // ✅ FIX: Updated PatientController with 4 parameters
        this.patientController = new PatientController(userService, appointmentService, billingService, paymentService);
        this.doctorController = new DoctorController(userService, appointmentService);
        this.adminController = new AdminController(userService, appointmentService, billingService, paymentService);
    }

    // PatientController methods
    public CommandResult registerPatient(String username, String password, String email, String phone,
                                         String firstName, String lastName, LocalDate dateOfBirth, Patient.Gender gender) {
        return patientController.registerPatient(username, password, email, phone, firstName, lastName, dateOfBirth, gender);
    }

    public CommandResult registerPatientFull(String username, String password, String email, String phone,
                                             String firstName, String lastName, LocalDate dateOfBirth, Patient.Gender gender,
                                             String bloodGroup, String address, String emergencyContactName, String emergencyContactPhone) {
        return patientController.registerPatientFull(username, password, email, phone, firstName, lastName,
                dateOfBirth, gender, bloodGroup, address, emergencyContactName, emergencyContactPhone);
    }

    public CommandResult bookAppointment(Long patientId, Long doctorId, LocalDate appointmentDate,
                                         LocalTime appointmentTime, String reason) {
        return patientController.bookAppointment(patientId, doctorId, appointmentDate, appointmentTime, reason);
    }

    public CommandResult viewPatientAppointments(Long patientId) {
        return patientController.viewAppointments(patientId);
    }

    // DoctorController methods
    public CommandResult updateDoctorProfile(Long doctorId, String firstName, String lastName, String email,
                                             String phone, String specialization) {
        return doctorController.updateProfile(doctorId, firstName, lastName, email, phone, specialization);
    }

    public CommandResult viewDoctorSchedule(Long doctorId) {
        return doctorController.viewSchedule(doctorId);
    }

    // AdminController methods
    public CommandResult viewAllUsers(Long adminId) {
        return adminController.viewAllUsers(adminId);
    }

    public CommandResult getUserDetails(Long adminId, Long userId) {
        return adminController.getUserDetails(adminId, userId);
    }

    public CommandResult deleteUser(Long adminId, Long userId) {
        return adminController.deleteUser(adminId, userId);
    }

    public CommandResult generateReport(Long adminId, ReportType type) {
        return adminController.generateReport(adminId, type);
    }

    public CommandResult generateDashboardSummary(Long adminId) {
        return adminController.generateDashboardSummary(adminId);
    }

    // Add these methods to MainController class

    // Admin appointment management
    public CommandResult viewAllAppointments(Long adminId) {
        return adminController.viewAllAppointments(adminId);
    }

    // Admin department management
    public CommandResult viewAllDepartments(Long adminId) {
        return adminController.viewAllDepartments(adminId);
    }

    public CommandResult addDepartment(Long adminId, String name, String description, String location, String phone) {
        return adminController.addDepartment(adminId, name, description, location, phone);
    }

    public CommandResult deleteDepartment(Long adminId, Long departmentId) {
        return adminController.deleteDepartment(adminId, departmentId);
    }

    // Admin doctor management
    public CommandResult addDoctor(Long adminId, String username, String password, String email, String phone,
                                   String firstName, String lastName, String specialization, String licenseNumber,
                                   Long departmentId, String qualification, int experienceYears, BigDecimal consultationFee) {
        return adminController.addDoctor(adminId, username, password, email, phone, firstName, lastName,
                specialization, licenseNumber, departmentId, qualification, experienceYears, consultationFee);
    }

    // Admin profile management
    public CommandResult viewAdminProfile(Long adminId) {
        return adminController.viewAdminProfile(adminId);
    }

    public CommandResult changeAdminPassword(Long adminId, String oldPassword, String newPassword) {
        return adminController.changeAdminPassword(adminId, oldPassword, newPassword);
    }
}
