package com.hospital.management.services.impl;

import com.hospital.management.commands.CommandResult;
import com.hospital.management.common.enums.AppointmentStatus;
import com.hospital.management.interfaces.AppointmentService;
import com.hospital.management.dao.interfaces.AppointmentDAO;
import com.hospital.management.dao.impl.AppointmentDAOImpl;
import com.hospital.management.models.Appointment;
import com.hospital.management.common.exceptions.ValidationException;
import com.hospital.management.common.exceptions.BusinessLogicException;
import com.hospital.management.common.exceptions.DatabaseException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentDAO appointmentDAO = new AppointmentDAOImpl();

    @Override
    public Optional<Appointment> findAppointmentById(Long id) {
        if (id == null) return Optional.empty();
        Appointment appointment = appointmentDAO.getAppointmentById(id.intValue());
        return Optional.ofNullable(appointment);
    }

    @Override
    public List<Appointment> getAppointmentsByPatient(Long patientId) {
        if (patientId == null) {
            return List.of();
        }
        // Use the dedicated DAO method instead of filtering all appointments
        return appointmentDAO.getAppointmentsByUserId(patientId);
    }


    @Override
    public List<Appointment> getAppointmentsByDoctor(Long doctorId) {
        if (doctorId == null) return List.of();
        return appointmentDAO.getAllAppointments().stream()
                .filter(a -> doctorId.equals(a.getDoctorId()))
                .collect(Collectors.toList());
    }

    // ✅ CHANGE THIS TO RETURN CommandResult:
    @Override
    public CommandResult bookAppointment(Long patientId, Long doctorId, LocalDate date, LocalTime time, String reason) {
        try {
            Appointment appointment = new Appointment(patientId, doctorId, date, time, reason);

            // Validate appointment
            appointment.validate();
            checkForConflicts(appointment);

            // Create appointment in database
            boolean created = appointmentDAO.createAppointment(appointment);

            if (created) {
                // ✅ CRITICAL: Get the appointment back from database with its generated ID
                List<Appointment> recentAppointments = appointmentDAO.getAllAppointments().stream()
                        .filter(a -> patientId.equals(a.getPatientId()) &&
                                doctorId.equals(a.getDoctorId()) &&
                                date.equals(a.getAppointmentDate()) &&
                                time.equals(a.getAppointmentTime()))
                        .collect(Collectors.toList());

                if (!recentAppointments.isEmpty()) {
                    Appointment createdAppointment = recentAppointments.get(recentAppointments.size() - 1); // Get latest
                    System.out.println("✅ DEBUG: Appointment created in service with ID: " + createdAppointment.getId());
                    return CommandResult.success("Appointment booked successfully", createdAppointment);
                } else {
                    return CommandResult.failure("Appointment created but could not retrieve ID", null);
                }
            } else {
                return CommandResult.failure("Failed to create appointment in database", null);
            }

        } catch (ValidationException e) {
            return CommandResult.failure("Validation error: " + e.getMessage(), null);
        } catch (BusinessLogicException e) {
            return CommandResult.failure("Business logic error: " + e.getMessage(), null);
        } catch (Exception e) {
            return CommandResult.failure("Unexpected error: " + e.getMessage(), null);
        }
    }



    @Override
    public boolean updateAppointment(Appointment appointment) {
        if (appointment == null || appointment.getId() == null) return false;
        try {
            appointment.validate();
            checkForConflicts(appointment);
            return appointmentDAO.updateAppointment(appointment);
        } catch (ValidationException | BusinessLogicException e) {
            System.err.println("Failed to update appointment: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean cancelAppointment(Long appointmentId) {
        if (appointmentId == null) return false;
        Optional<Appointment> optionalAppointment = findAppointmentById(appointmentId);
        if (optionalAppointment.isEmpty()) return false;

        Appointment appointment = optionalAppointment.get();
        if (!appointment.canBeCancelled()) {
            System.err.println("Cannot cancel appointment: status or timing invalid");
            return false;
        }
        appointment.markAsCancelled();
        return appointmentDAO.updateAppointment(appointment);
    }

    @Override
    public CommandResult updateAppointmentStatus(Long appointmentId, AppointmentStatus status) {
        try {
            boolean updated = appointmentDAO.updateAppointmentStatus(appointmentId, status);
            if (updated) {
                return CommandResult.success("Appointment status updated successfully", null);
            } else {
                return CommandResult.failure("Failed to update appointment status", null);
            }
        } catch (Exception e) {
            return CommandResult.failure("Error updating appointment status: " + e.getMessage(), null);
        }
    }

    @Override
    public List<Appointment> getAllAppointments() {
        return appointmentDAO.getAllAppointments();
    }

    /**
     * Check that there are no time conflicts for the given appointment
     * Throws BusinessLogicException if conflict is found
     */
    private void checkForConflicts(Appointment appointment) throws BusinessLogicException {
        List<Appointment> appointmentsForDoctor = getAppointmentsByDoctor(appointment.getDoctorId());

        boolean conflict = appointmentsForDoctor.stream()
                .filter(a -> !a.getId().equals(appointment.getId())) // ignore same appointment when updating
                .anyMatch(existing -> existing.getAppointmentDate().equals(appointment.getAppointmentDate())
                        && existing.getAppointmentTime().equals(appointment.getAppointmentTime())
                        && existing.getStatus().isActive());

        if (conflict) {
            throw new BusinessLogicException("Doctor already has an appointment at this time", "AppointmentConflict", "Appointment");
        }
    }
}
