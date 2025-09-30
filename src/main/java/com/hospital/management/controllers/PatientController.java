package com.hospital.management.controllers;

import com.hospital.management.commands.Command;
import com.hospital.management.commands.CommandResult;
import com.hospital.management.commands.PatientCommands.*;
import com.hospital.management.interfaces.*;
import com.hospital.management.services.impl.*;
import com.hospital.management.models.*;
import com.hospital.management.common.enums.PaymentMethod;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Controller handling patient-related activities by invoking commands.
 */
public class PatientController {
    private final UserService userService;
    private final AppointmentService appointmentService;
    private final BillingService billingService;           // ✅ ADD THIS
    private final PaymentService paymentService;           // ✅ ADD THIS
    private final DepartmentService departmentService;     // ✅ ADD THIS
    private final DoctorService doctorService;             // ✅ ADD THIS

    // ✅ UPDATED CONSTRUCTOR:
    public PatientController(UserService userService, AppointmentService appointmentService,
                             BillingService billingService, PaymentService paymentService) {
        this.userService = userService;
        this.appointmentService = appointmentService;
        this.billingService = billingService;
        this.paymentService = paymentService;
        this.departmentService = new DepartmentServiceImpl();
        this.doctorService = new DoctorServiceImpl();
    }

    // ✅ EXISTING METHODS (keep all your current methods)
    public CommandResult registerPatient(String username, String password, String email, String phone,
                                         String firstName, String lastName, LocalDate dateOfBirth, Patient.Gender gender) {
        Command command = new RegisterPatientCommand(
                username, password, email, phone,
                firstName, lastName, dateOfBirth, gender, userService);
        try {
            return command.execute();
        } catch (Exception e) {
            return CommandResult.failure("Error during patient registration: " + e.getMessage(), e);
        }
    }

    public CommandResult registerPatientFull(String username, String password, String email, String phone,
                                             String firstName, String lastName, LocalDate dateOfBirth, Patient.Gender gender,
                                             String bloodGroup, String address,
                                             String emergencyContactName, String emergencyContactPhone) {
        Command command = new RegisterPatientCommand(
                username, password, email, phone,
                firstName, lastName, dateOfBirth, gender,
                bloodGroup, address, emergencyContactName, emergencyContactPhone,
                userService);
        try {
            return command.execute();
        } catch (Exception e) {
            return CommandResult.failure("Error during full patient registration: " + e.getMessage(), e);
        }
    }

    public CommandResult updatePatientProfile(Long patientId, String firstName, String lastName,
                                              String email, String phone, LocalDate dateOfBirth,
                                              Patient.Gender gender, String bloodGroup, String address,
                                              String emergencyContactName, String emergencyContactPhone) {
        Command command = new UpdatePatientProfileCommand(
                patientId, firstName, lastName, email, phone, dateOfBirth, gender,
                bloodGroup, address, emergencyContactName, emergencyContactPhone, userService);
        try {
            return command.execute();
        } catch (Exception e) {
            return CommandResult.failure("Error updating patient profile: " + e.getMessage(), e);
        }
    }

    public CommandResult updatePatientProfile(Long patientId, String firstName, String lastName,
                                              String email, String phone) {
        Command command = new UpdatePatientProfileCommand(patientId, firstName, lastName,
                email, phone, userService);
        try {
            return command.execute();
        } catch (Exception e) {
            return CommandResult.failure("Error updating patient profile: " + e.getMessage(), e);
        }
    }

    // ✅ UPDATED: Use new BookAppointmentCommand with payment support
    public CommandResult bookAppointment(Long patientId, Long doctorId, LocalDate appointmentDate,
                                         LocalTime appointmentTime, String reason) {
        // Default to CASH payment for backward compatibility
        return bookAppointmentWithPayment(patientId, doctorId, appointmentDate, appointmentTime, reason, PaymentMethod.CASH);
    }

    // ✅ NEW: Enhanced booking with payment method
    public CommandResult bookAppointmentWithPayment(Long patientId, Long doctorId, LocalDate appointmentDate,
                                                    LocalTime appointmentTime, String reason, PaymentMethod paymentMethod) {
        Command command = new BookAppointmentCommand(patientId, doctorId, appointmentDate, appointmentTime,
                reason, paymentMethod, appointmentService, doctorService, billingService, paymentService);
        try {
            return command.execute();
        } catch (Exception e) {
            return CommandResult.failure("Error booking appointment: " + e.getMessage(), e);
        }
    }

    public CommandResult viewAppointments(Long patientId) {
        Command command = new ViewAppointmentsCommand(patientId, appointmentService);
        try {
            return command.execute();
        } catch (Exception e) {
            return CommandResult.failure("Error viewing appointments: " + e.getMessage(), e);
        }
    }

    public CommandResult viewPatientProfile(Long patientId) {
        Command command = new ViewPatientProfileCommand(patientId, userService);
        try {
            return command.execute();
        } catch (Exception e) {
            return CommandResult.failure("Error retrieving patient profile: " + e.getMessage(), e);
        }
    }

    public CommandResult viewPatientBills(Long patientId) {
        Command command = new ViewPatientBillsCommand(patientId);
        try {
            return command.execute();
        } catch (Exception e) {
            return CommandResult.failure("Error retrieving bills and payments: " + e.getMessage(), e);
        }
    }

    // ✅ ADD THESE NEW METHODS FOR UI SUPPORT:
    public CommandResult getAllDepartments() {
        try {
            List<Department> departments = departmentService.getDepartmentsWithDoctors();
            return CommandResult.success("Departments retrieved successfully", departments);
        } catch (Exception e) {
            return CommandResult.failure("Error retrieving departments: " + e.getMessage(), null);
        }
    }

    public CommandResult getDoctorsByDepartment(Long departmentId) {
        try {
            List<Doctor> doctors = doctorService.getDoctorsByDepartment(departmentId);
            return CommandResult.success("Doctors retrieved successfully", doctors);
        } catch (Exception e) {
            return CommandResult.failure("Error retrieving doctors: " + e.getMessage(), null);
        }
    }


    public CommandResult getAvailableTimeSlots(Long doctorId, LocalDate appointmentDate) {
        try {
            List<LocalTime> availableSlots = doctorService.getAvailableTimeSlots(doctorId, appointmentDate);
            return CommandResult.success("Available time slots retrieved", availableSlots);
        } catch (Exception e) {
            return CommandResult.failure("Error retrieving time slots: " + e.getMessage(), null);
        }
    }
}
