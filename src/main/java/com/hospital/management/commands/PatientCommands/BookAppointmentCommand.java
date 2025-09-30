package com.hospital.management.commands.PatientCommands;

import com.hospital.management.commands.Command;
import com.hospital.management.commands.CommandResult;
import com.hospital.management.common.exceptions.DatabaseException;
import com.hospital.management.common.exceptions.ValidationException;
import com.hospital.management.common.exceptions.BusinessLogicException;
import com.hospital.management.interfaces.AppointmentService;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Command to book a new appointment for a patient
 * Uses AppointmentService for booking
 */
public class BookAppointmentCommand implements Command {

    private final Long patientId;
    private final Long doctorId;
    private final LocalDate appointmentDate;
    private final LocalTime appointmentTime;
    private final String reason;

    // Service dependency
    private final AppointmentService appointmentService;

    public BookAppointmentCommand(Long patientId, Long doctorId, LocalDate appointmentDate,
                                  LocalTime appointmentTime, String reason,
                                  AppointmentService appointmentService) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.reason = reason;
        this.appointmentService = appointmentService;
    }

    @Override
    public CommandResult execute() throws DatabaseException, ValidationException, BusinessLogicException {
        try {
            // Validate parameters first
            if (!validateParameters()) {
                return CommandResult.failure("Invalid parameters for appointment booking");
            }

            // Use service to book appointment - matches the interface signature
            boolean success = appointmentService.bookAppointment(patientId, doctorId, appointmentDate, appointmentTime, reason);

            if (success) {
                return CommandResult.success("Appointment booked successfully with doctor ID " + doctorId +
                        " on " + appointmentDate + " at " + appointmentTime);
            } else {
                throw new BusinessLogicException("Failed to book appointment");
            }

        } catch (ValidationException | BusinessLogicException e) {
            return CommandResult.failure("Appointment booking failed: " + e.getMessage(), e);
        } catch (Exception e) {
            return CommandResult.failure("Unexpected error during appointment booking: " + e.getMessage(), e);
        }
    }

    @Override
    public String getDescription() {
        return "Book appointment for patient ID " + patientId + " with doctor ID " + doctorId +
                " on " + appointmentDate + " at " + appointmentTime;
    }

    @Override
    public boolean validateParameters() throws ValidationException {
        if (appointmentService == null) {
            throw new ValidationException("AppointmentService is required", "AppointmentService");
        }

        if (patientId == null || patientId <= 0) {
            throw new ValidationException("Valid patient ID is required", "PatientId", patientId);
        }

        if (doctorId == null || doctorId <= 0) {
            throw new ValidationException("Valid doctor ID is required", "DoctorId", doctorId);
        }

        if (appointmentDate == null) {
            throw new ValidationException("Appointment date is required", "AppointmentDate");
        }

        if (appointmentTime == null) {
            throw new ValidationException("Appointment time is required", "AppointmentTime");
        }

        // Basic date validation - let service handle business rules
        if (appointmentDate.isBefore(LocalDate.now())) {
            throw new ValidationException("Appointment date cannot be in the past", "AppointmentDate", appointmentDate);
        }

        return true;
    }
}
