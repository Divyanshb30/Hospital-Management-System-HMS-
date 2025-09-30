package com.hospital.management.controllers;

import com.hospital.management.commands.Command;
import com.hospital.management.commands.CommandResult;
import com.hospital.management.commands.PatientCommands.RegisterPatientCommand;
import com.hospital.management.commands.PatientCommands.BookAppointmentCommand;
import com.hospital.management.commands.PatientCommands.ViewAppointmentsCommand;
import com.hospital.management.interfaces.UserService;
import com.hospital.management.interfaces.AppointmentService;
import com.hospital.management.models.Patient;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Controller handling patient-related activities by invoking commands.
 */
public class PatientController {

    private final UserService userService;
    private final AppointmentService appointmentService;

    public PatientController(UserService userService, AppointmentService appointmentService) {
        this.userService = userService;
        this.appointmentService = appointmentService;
    }

    // Registers a new patient (minimum required fields)
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

    // Optional: Register patient with additional fields (overloaded method)
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

    // Books an appointment for a patient
    public CommandResult bookAppointment(Long patientId, Long doctorId, LocalDate appointmentDate,
                                         LocalTime appointmentTime, String reason) {
        Command command = new BookAppointmentCommand(
                patientId, doctorId, appointmentDate, appointmentTime, reason, appointmentService);
        try {
            return command.execute();
        } catch (Exception e) {
            return CommandResult.failure("Error booking appointment: " + e.getMessage(), e);
        }
    }

    // Views appointments for a patient
    public CommandResult viewAppointments(Long patientId) {
        Command command = new ViewAppointmentsCommand(patientId, appointmentService);
        try {
            return command.execute();
        } catch (Exception e) {
            return CommandResult.failure("Error viewing appointments: " + e.getMessage(), e);
        }
    }
}
