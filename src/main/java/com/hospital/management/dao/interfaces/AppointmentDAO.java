package com.hospital.management.dao.interfaces;

// dao/interfaces/AppointmentDAO.java

import com.hospital.management.models.Appointment;
import java.util.List;
import java.time.LocalDate;
import com.hospital.management.common.enums.AppointmentStatus;

public interface AppointmentDAO {
    Appointment getAppointmentById(int id);
    List<Appointment> getAllAppointments();
    boolean createAppointment(Appointment appointment);
    boolean updateAppointment(Appointment appointment);
    boolean deleteAppointment(int id);
    List<Appointment> getAppointmentsByDoctorAndDate(Long doctorId, LocalDate appointmentDate);
    boolean updateAppointmentStatus(Long appointmentId, AppointmentStatus status);
    List<Appointment> getAppointmentsByUserId(Long patientId);
}
