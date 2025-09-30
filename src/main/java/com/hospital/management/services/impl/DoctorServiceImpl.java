package com.hospital.management.services.impl;

import com.hospital.management.interfaces.DoctorService;
import com.hospital.management.dao.interfaces.DoctorDAO;
import com.hospital.management.dao.interfaces.AppointmentDAO;
import com.hospital.management.dao.impl.DoctorDAOImpl;
import com.hospital.management.dao.impl.AppointmentDAOImpl;
import com.hospital.management.models.Doctor;
import com.hospital.management.models.Appointment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

public class DoctorServiceImpl implements DoctorService {
    private final DoctorDAO doctorDAO = new DoctorDAOImpl();
    private final AppointmentDAO appointmentDAO = new AppointmentDAOImpl();

    @Override
    public List<Doctor> getDoctorsByDepartment(Long departmentId) {
        return doctorDAO.getDoctorsByDepartment(departmentId);
    }

    @Override
    public Optional<Doctor> findDoctorById(Long doctorId) {
        if (doctorId == null) return Optional.empty();
        Doctor doctor = doctorDAO.getDoctorById(doctorId.intValue());
        return Optional.ofNullable(doctor);
    }

    @Override
    public List<LocalTime> getAvailableTimeSlots(Long doctorId, LocalDate appointmentDate) {
        List<LocalTime> allSlots = generateTimeSlots();
        List<Appointment> bookedAppointments = appointmentDAO.getAppointmentsByDoctorAndDate(
                doctorId, appointmentDate);

        List<LocalTime> bookedTimes = bookedAppointments.stream()
                .map(Appointment::getAppointmentTime)
                .toList();

        return allSlots.stream()
                .filter(slot -> !bookedTimes.contains(slot))
                .toList();
    }

    @Override
    public boolean isDoctorAvailable(Long doctorId, LocalDate date, LocalTime time) {
        List<LocalTime> availableSlots = getAvailableTimeSlots(doctorId, date);
        return availableSlots.contains(time);
    }

    @Override
    public Doctor getDoctorById(Long doctorId) {
        return doctorDAO.getDoctorById(doctorId.intValue());
    }

    private List<LocalTime> generateTimeSlots() {
        List<LocalTime> slots = new ArrayList<>();
        for (int hour = 10; hour <= 17; hour++) {
            slots.add(LocalTime.of(hour, 0));
        }
        return slots;
    }

}
