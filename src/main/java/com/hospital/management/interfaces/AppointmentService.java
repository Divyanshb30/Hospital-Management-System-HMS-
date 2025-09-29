package com.hospital.management.interfaces;

import com.hospital.management.models.Appointment;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentService {
    Optional<Appointment> findAppointmentById(Long id);
    List<Appointment> getAppointmentsByPatient(Long patientId);
    List<Appointment> getAppointmentsByDoctor(Long doctorId);
    boolean bookAppointment(Long patientId, Long doctorId, LocalDate date, LocalTime time, String reason);
    boolean updateAppointment(Appointment appointment);
    boolean cancelAppointment(Long appointmentId);
    List<Appointment> getAllAppointments();
}
