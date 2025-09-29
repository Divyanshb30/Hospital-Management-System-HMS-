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

    @Override
    public int createDoctor(Doctor doctor) {
        return doctorDAO.addDoctor(doctor);
    }

    @Override
    public Doctor getDoctor(int id) {
        return doctorDAO.getDoctorById(id);
    }

    @Override
    public List<Doctor> getAllDoctors() {
        return doctorDAO.getAllDoctors();
    }

    @Override
    public void updateDoctor(Doctor doctor) {
        doctorDAO.updateDoctor(doctor);
    }

    @Override
    public void deleteDoctor(int id) {
        doctorDAO.deleteDoctor(id);
    }

    @Override
    public int addSpecialization(String name, String description) {
        DoctorSpecialization spec = new DoctorSpecialization(name, description);
        return specializationDAO.addSpecialization(spec);
    }

    @Override
    public List<DoctorSpecialization> listSpecializations() {
        return specializationDAO.getAll();
    }

    @Override
    public void deleteSpecialization(int id) {
        specializationDAO.delete(id);
    }

    @Override
    public int addSchedule(DoctorSchedule schedule) {
        return scheduleDAO.addSchedule(schedule);
    }

    @Override
    public List<DoctorSchedule> getSchedulesForDoctor(int doctorId) {
        return scheduleDAO.getSchedulesForDoctor(doctorId);
    }

    @Override
    public void deleteSchedule(int scheduleId) {
        scheduleDAO.deleteSchedule(scheduleId);
    }

    @Override
    public DoctorStats computeStats(int doctorId) {
        return statsDAO.computeStatsForDoctor(doctorId);
    }

    @Override
    public void refreshAndStoreStats(int doctorId) {
        DoctorStats stats = computeStats(doctorId);
        statsDAO.upsertStats(stats);
    }
}
