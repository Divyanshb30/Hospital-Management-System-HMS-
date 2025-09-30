package com.hospital.management.controllers;

import com.hospital.management.commands.Command;
import com.hospital.management.commands.CommandResult;
import com.hospital.management.commands.DoctorCommands.UpdateProfileCommand;
import com.hospital.management.commands.DoctorCommands.ViewScheduleCommand;
import com.hospital.management.interfaces.UserService;
import com.hospital.management.interfaces.AppointmentService;

/**
 * Controller to handle doctor-related actions by executing commands.
 */
public class DoctorController {

    private final UserService userService;
    private final AppointmentService appointmentService;

    public DoctorController(UserService userService, AppointmentService appointmentService) {
        this.userService = userService;
        this.appointmentService = appointmentService;
    }

    /**
     * Updates doctor profile info with provided fields
     */
    public CommandResult updateProfile(Long doctorId, String firstName, String lastName,
                                       String email, String phone, String specialization) {
        Command command = new UpdateProfileCommand(
                doctorId, firstName, lastName, email, phone, specialization, userService);
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
}
