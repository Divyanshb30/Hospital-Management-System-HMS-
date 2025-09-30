package com.hospital.management.commands.DoctorCommands;

import com.hospital.management.commands.Command;
import com.hospital.management.commands.CommandResult;
import com.hospital.management.common.exceptions.DatabaseException;
import com.hospital.management.common.exceptions.ValidationException;
import com.hospital.management.common.exceptions.BusinessLogicException;
import com.hospital.management.interfaces.AppointmentService;
import com.hospital.management.models.Appointment;
import java.util.List;

/**
 * Command to view doctor's schedule/appointments
 * Uses AppointmentService to retrieve doctor's appointments
 */
public class ViewScheduleCommand implements Command {

    private final Long doctorId;

    // Service dependency
    private final AppointmentService appointmentService;

    public ViewScheduleCommand(Long doctorId, AppointmentService appointmentService) {
        this.doctorId = doctorId;
        this.appointmentService = appointmentService;
    }

    @Override
    public CommandResult execute() throws DatabaseException, ValidationException, BusinessLogicException {
        try {
            // Validate parameters first
            if (!validateParameters()) {
                return CommandResult.failure("Invalid parameters for viewing schedule");
            }

            // Use service to get doctor's appointments
            List<Appointment> appointments = appointmentService.getAppointmentsByDoctor(doctorId);

            String resultMessage = "Found " + appointments.size() + " appointments in schedule";
            return CommandResult.success(resultMessage, appointments);

        } catch (ValidationException e) {
            return CommandResult.failure("Failed to view schedule: " + e.getMessage(), e);
        } catch (Exception e) {
            return CommandResult.failure("Unexpected error while viewing schedule: " + e.getMessage(), e);
        }
    }

    @Override
    public String getDescription() {
        return "View schedule for doctor ID " + doctorId;
    }

    @Override
    public boolean validateParameters() throws ValidationException {
        if (appointmentService == null) {
            throw new ValidationException("AppointmentService is required", "AppointmentService");
        }

        if (doctorId == null || doctorId <= 0) {
            throw new ValidationException("Valid doctor ID is required", "DoctorId", doctorId);
        }

        return true;
    }
}
