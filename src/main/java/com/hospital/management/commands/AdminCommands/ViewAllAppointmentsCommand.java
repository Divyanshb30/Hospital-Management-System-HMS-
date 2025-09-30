package com.hospital.management.commands.AdminCommands;

import com.hospital.management.commands.Command;
import com.hospital.management.commands.CommandResult;
import com.hospital.management.common.exceptions.DatabaseException;
import com.hospital.management.common.exceptions.ValidationException;
import com.hospital.management.dao.interfaces.AppointmentDAO;
import com.hospital.management.dao.impl.AppointmentDAOImpl;
import com.hospital.management.models.Appointment;

import java.util.List;

public class ViewAllAppointmentsCommand implements Command {

    private final Long adminId;
    private final AppointmentDAO appointmentDAO;

    public ViewAllAppointmentsCommand(Long adminId) {
        this.adminId = adminId;
        this.appointmentDAO = new AppointmentDAOImpl();
    }

    @Override
    public CommandResult execute() throws ValidationException, DatabaseException {
        if (!validateParameters()) {
            throw new ValidationException("Invalid admin ID", "AdminId");
        }

        try {
            List<Appointment> appointments = appointmentDAO.getAllAppointments();
            return CommandResult.success("All appointments retrieved successfully", appointments);
        } catch (Exception e) {
            throw new DatabaseException("Error retrieving appointments: " + e.getMessage(), "APPOINTMENTS_RETRIEVAL_ERROR");
        }
    }

    @Override
    public String getDescription() {
        return "View all appointments in the system";
    }

    @Override
    public boolean validateParameters() throws ValidationException {
        return adminId != null && adminId > 0;
    }
}
