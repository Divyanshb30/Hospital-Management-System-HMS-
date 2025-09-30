package com.hospital.management.services.impl;

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
        if (patientId == null) return List.of();
        return appointmentDAO.getAllAppointments().stream()
                .filter(a -> patientId.equals(a.getPatientId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Appointment> getAppointmentsByDoctor(Long doctorId) {
        if (doctorId == null) return List.of();
        return appointmentDAO.getAllAppointments().stream()
                .filter(a -> doctorId.equals(a.getDoctorId()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean bookAppointment(Long patientId, Long doctorId, LocalDate date, LocalTime time, String reason) {
        Appointment appointment = new Appointment(patientId, doctorId, date, time, reason);
        try {
            appointment.validate();
            checkForConflicts(appointment);
            return appointmentDAO.createAppointment(appointment);
        } catch (ValidationException | BusinessLogicException e) {
            System.err.println("Failed to book appointment: " + e.getMessage());
            return false;
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
