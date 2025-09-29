package com.hospital.management.dao.interfaces;

// dao/interfaces/AppointmentDAO.java

import com.hospital.management.models.Appointment;
import java.util.List;

public interface AppointmentDAO {
    Appointment getAppointmentById(int id);
    List<Appointment> getAllAppointments();
    boolean createAppointment(Appointment appointment);
    boolean updateAppointment(Appointment appointment);
    boolean deleteAppointment(int id);
}
