package com.hospital.management.commands.PatientCommands;

import com.hospital.management.commands.Command;
import com.hospital.management.commands.CommandResult;
import com.hospital.management.common.exceptions.DatabaseException;
import com.hospital.management.common.exceptions.ValidationException;
import com.hospital.management.common.exceptions.BusinessLogicException;
import com.hospital.management.interfaces.AppointmentService;
import com.hospital.management.models.Appointment;
import java.util.List;

/**
 * Command to view appointments for a specific patient
 * Uses AppointmentService to retrieve appointments
 */
public class ViewAppointmentsCommand implements Command {

    private final Long patientId;

    // Service dependency
    private final AppointmentService appointmentService;

    public ViewAppointmentsCommand(Long patientId, AppointmentService appointmentService) {
        this.patientId = patientId;
        this.appointmentService = appointmentService;
    }

    @Override
    public CommandResult execute() throws DatabaseException, ValidationException, BusinessLogicException {
        try {
            // Validate parameters first
            if (!validateParameters()) {
                return CommandResult.failure("Invalid parameters for viewing appointments");
            }

            // Use service to get patient's appointments
            List<Appointment> appointments = appointmentService.getAppointmentsByPatient(patientId);

            String resultMessage = "Found " + appointments.size() + " appointments";
            return CommandResult.success(resultMessage, appointments);

        } catch (ValidationException e) {
            return CommandResult.failure("Failed to view appointments: " + e.getMessage(), e);
        } catch (Exception e) {
            return CommandResult.failure("Unexpected error while viewing appointments: " + e.getMessage(), e);
        }
    }

    @Override
    public String getDescription() {
        return "View appointments for patient ID " + patientId;
    }

    @Override
    public boolean validateParameters() throws ValidationException {
        if (appointmentService == null) {
            throw new ValidationException("AppointmentService is required", "AppointmentService");
        }

        if (patientId == null || patientId <= 0) {
            throw new ValidationException("Valid patient ID is required", "PatientId", patientId);
        }

        return true;
    }
}
