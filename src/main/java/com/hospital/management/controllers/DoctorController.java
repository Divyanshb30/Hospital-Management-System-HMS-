package com.hospital.management.controllers;

import com.hospital.management.commands.Command;
import com.hospital.management.commands.CommandResult;
import com.hospital.management.commands.DoctorCommands.UpdateProfileCommand;
import com.hospital.management.commands.DoctorCommands.ViewScheduleCommand;
import com.hospital.management.interfaces.UserService;
import com.hospital.management.interfaces.AppointmentService;
import com.hospital.management.interfaces.DoctorService;  // ✅ ADD THIS IMPORT
import com.hospital.management.models.Doctor;
import com.hospital.management.services.impl.DoctorServiceImpl;  // ✅ ADD THIS IMPORT

import java.util.Optional;

/**
 * Controller to handle doctor-related actions by executing commands.
 */
public class DoctorController {

    private final UserService userService;
    private final AppointmentService appointmentService;
    private final DoctorService doctorService;  // ✅ ADD THIS FIELD

    public DoctorController(UserService userService, AppointmentService appointmentService) {
        this.userService = userService;
        this.appointmentService = appointmentService;
        this.doctorService = new DoctorServiceImpl();  // ✅ ADD THIS INITIALIZATION
    }

    /**
     * Updates doctor profile info with provided fields
     */
    public CommandResult updateProfile(Long doctorId, String firstName, String lastName,
                                       String email, String phone, String specialization) {
        // ✅ CHANGED: Pass doctorService instead of userService
        Command command = new UpdateProfileCommand(
                doctorId, firstName, lastName, email, phone, specialization, doctorService);
        try {
            return command.execute();
        } catch (Exception e) {
            return CommandResult.failure("Error updating doctor profile: " + e.getMessage(), e);
        }
    }

    /**
     * Views appointments scheduled for given doctor
     */
    public CommandResult viewSchedule(Long doctorId) {
        Command command = new ViewScheduleCommand(doctorId, appointmentService);
        try {
            return command.execute();
        } catch (Exception e) {
            return CommandResult.failure("Error viewing doctor schedule: " + e.getMessage(), e);
        }
    }

    /**
     * Updates doctor qualification
     */
    public CommandResult updateQualification(Long doctorId, String qualification) {
        try {
            // Use DoctorService to update qualification
            boolean success = doctorService.updateDoctorQualification(doctorId, qualification);

            if (success) {
                return CommandResult.success("Qualification updated successfully");
            } else {
                return CommandResult.failure("Failed to update qualification");
            }

        } catch (Exception e) {
            return CommandResult.failure("Error updating qualification: " + e.getMessage(), e);
        }
    }

    /**
     * Gets doctor's current consultation fee
     */
    public CommandResult getConsultationFees(Long doctorId) {
        try {
            Optional<Doctor> doctorOpt = doctorService.findDoctorById(doctorId);

            if (doctorOpt.isEmpty()) {
                return CommandResult.failure("Doctor not found");
            }

            Doctor doctor = doctorOpt.get();
            java.math.BigDecimal consultationFee = doctor.getConsultationFee();

            if (consultationFee == null) {
                consultationFee = new java.math.BigDecimal("1000.00"); // Default fee
            }

            return CommandResult.success("Consultation fee retrieved successfully", consultationFee);

        } catch (Exception e) {
            return CommandResult.failure("Error retrieving consultation fee: " + e.getMessage(), e);
        }
    }

    /**
     * Updates doctor's consultation fee
     */
    public CommandResult updateConsultationFee(Long doctorId, java.math.BigDecimal newFee) {
        try {
            boolean success = doctorService.updateConsultationFee(doctorId, newFee);

            if (success) {
                return CommandResult.success("Consultation fee updated successfully");
            } else {
                return CommandResult.failure("Failed to update consultation fee");
            }

        } catch (Exception e) {
            return CommandResult.failure("Error updating consultation fee: " + e.getMessage(), e);
        }
    }


}
