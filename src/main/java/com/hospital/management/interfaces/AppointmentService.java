package com.hospital.management.interfaces;

import com.hospital.management.commands.CommandResult;
import com.hospital.management.models.Appointment;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import com.hospital.management.common.enums.AppointmentStatus;


public interface AppointmentService {
    Optional<Appointment> findAppointmentById(Long id);
    List<Appointment> getAppointmentsByPatient(Long patientId);
    List<Appointment> getAppointmentsByDoctor(Long doctorId);

    CommandResult bookAppointment(Long patientId, Long doctorId, LocalDate date, LocalTime time, String reason);
    boolean updateAppointment(Appointment appointment);
    boolean cancelAppointment(Long appointmentId);
    List<Appointment> getAllAppointments();
    CommandResult updateAppointmentStatus(Long appointmentId, AppointmentStatus status);

}
