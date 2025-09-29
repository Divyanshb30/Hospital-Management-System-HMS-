package com.hospital.management.doctor.service;

import com.hospital.management.doctor.dao.*;
import com.hospital.management.doctor.model.*;

import java.util.List;

public class DoctorServiceImpl implements DoctorService {

    private final DoctorDAO doctorDAO;
    private final DoctorSpecializationDAO specializationDAO;
    private final DoctorScheduleDAO scheduleDAO;
    private final DoctorStatsDAO statsDAO;

    public DoctorServiceImpl() {
        this.doctorDAO = new DoctorDAOImpl();
        this.specializationDAO = new DoctorSpecializationDAOImpl();
        this.scheduleDAO = new DoctorScheduleDAOImpl();
        this.statsDAO = new DoctorStatsDAOImpl();
    }

    private void validateContactNumber(String contact) {
        if (contact == null || contact.trim().isEmpty()) {
            throw new IllegalArgumentException("Contact number cannot be null or empty");
        }
        // simple pattern: allows numbers, +, and -
        if (!contact.matches("^[0-9\\-+]{7,15}$")) {
            throw new IllegalArgumentException("Invalid contact number format");
        }
    }

    private void validateId(int id, String fieldName) {
        if (id <= 0) {
            throw new IllegalArgumentException(fieldName + " must be positive");
        }
    }

    @Override
    public int createDoctor(Doctor doctor) {
        if (doctor == null) {
            throw new IllegalArgumentException("Doctor cannot be null");
        }
        if (doctor.getName() == null || doctor.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Doctor name cannot be null or empty");
        }
        validateId(doctor.getSpecializationId(), "Specialization ID");
        validateContactNumber(doctor.getContact());
        return doctorDAO.addDoctor(doctor);
    }

    @Override
    public Doctor getDoctor(int id) {
        validateId(id, "Doctor ID");
        return doctorDAO.getDoctorById(id);
    }

    @Override
    public List<Doctor> getAllDoctors() {
        return doctorDAO.getAllDoctors();
    }

    @Override
    public void updateDoctor(Doctor doctor) {
        if (doctor == null) {
            throw new IllegalArgumentException("Doctor cannot be null");
        }
        validateId(doctor.getDoctorId(), "Doctor ID");
        if (doctor.getName() == null || doctor.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Doctor name cannot be null or empty");
        }
        validateId(doctor.getSpecializationId(), "Specialization ID");
        validateContactNumber(doctor.getContact());
        doctorDAO.updateDoctor(doctor);
    }

    @Override
    public void deleteDoctor(int id) {
        validateId(id, "Doctor ID");
        doctorDAO.deleteDoctor(id);
    }

    @Override
    public int addSpecialization(String name, String description) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Specialization name cannot be null or empty");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Specialization description cannot be null or empty");
        }
        DoctorSpecialization spec = new DoctorSpecialization(name.trim(), description.trim());
        return specializationDAO.addSpecialization(spec);
    }

    @Override
    public List<DoctorSpecialization> listSpecializations() {
        return specializationDAO.getAll();
    }

    @Override
    public boolean deleteSpecialization(int id) {
        validateId(id, "Specialization ID");
        return specializationDAO.delete(id);
    }

    @Override
    public int addSchedule(DoctorSchedule schedule) {
        if (schedule == null) {
            throw new IllegalArgumentException("Schedule cannot be null");
        }
        validateId(schedule.getDoctorId(), "Doctor ID");
        if (schedule.getDayOfWeek() == null || schedule.getDayOfWeek().trim().isEmpty()) {
            throw new IllegalArgumentException("Day of week cannot be null or empty");
        }
        if (schedule.getStartTime() == null || schedule.getEndTime() == null) {
            throw new IllegalArgumentException("Start and end times must be provided");
        }
        return scheduleDAO.addSchedule(schedule);
    }

    @Override
    public List<DoctorSchedule> getSchedulesForDoctor(int doctorId) {
        validateId(doctorId, "Doctor ID");
        return scheduleDAO.getSchedulesForDoctor(doctorId);
    }

    @Override
    public void deleteSchedule(int scheduleId) {
        validateId(scheduleId, "Schedule ID");
        scheduleDAO.deleteSchedule(scheduleId);
    }

    @Override
    public DoctorStats computeStats(int doctorId) {
        validateId(doctorId, "Doctor ID");
        return statsDAO.computeStatsForDoctor(doctorId);
    }

    @Override
    public void refreshAndStoreStats(int doctorId) {
        validateId(doctorId, "Doctor ID");
        DoctorStats stats = computeStats(doctorId);

        // example rating validation (0.0–5.0)
        if (stats.getAverageRating() < 0.0 || stats.getAverageRating() > 5.0) {
            throw new IllegalArgumentException("Average rating must be between 0.0 and 5.0");
        }

        statsDAO.upsertStats(stats);
    }
}
