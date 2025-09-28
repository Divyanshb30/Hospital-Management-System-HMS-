package com.hospital.management.doctor.service;

import com.hospital.management.doctor.dao.*;
import com.hospital.management.doctor.model.*;

import java.util.List;

public class DoctorServiceImpl implements DoctorService {

    private final DoctorDAO doctorDAO = new DoctorDAOImpl();
    private final DoctorSpecializationDAO specDAO = new DoctorSpecializationDAOImpl();
    private final DoctorScheduleDAO scheduleDAO = new DoctorScheduleDAOImpl();
    private final DoctorStatsDAO statsDAO = new DoctorStatsDAOImpl();

    @Override
    public int createDoctor(Doctor doctor) {
        // business rule: require name
        if (doctor.getName() == null || doctor.getName().isBlank()) {
            throw new IllegalArgumentException("Doctor name required");
        }
        return doctorDAO.addDoctor(doctor);
    }

    @Override public Doctor getDoctor(int id) { return doctorDAO.getDoctorById(id);}
    @Override public List<Doctor> getAllDoctors() { return doctorDAO.getAllDoctors(); }
    @Override public void updateDoctor(Doctor doctor) { doctorDAO.updateDoctor(doctor); }
    @Override public void deleteDoctor(int id) { doctorDAO.deleteDoctor(id); }

    // specializations
    @Override
    public int addSpecialization(String name, String description) {
        DoctorSpecialization s = new DoctorSpecialization(name, description);
        return specDAO.addSpecialization(s);
    }
    @Override public List<DoctorSpecialization> listSpecializations() { return specDAO.getAll(); }

    @Override
    public void deleteSpecialization(int id) {
        specDAO.delete(id);
    }


    // schedules
    @Override
    public int addSchedule(DoctorSchedule scheduleObj) {
        return scheduleDAO.addSchedule(scheduleObj);
    }
    @Override
    public List<DoctorSchedule> getSchedulesForDoctor(int doctorId) {
        return scheduleDAO.getSchedulesForDoctor(doctorId);

    }

    @Override
    public void deleteSchedule(int scheduleId) {
        scheduleDAO.deleteSchedule(scheduleId);
    }

    @Override public DoctorStats computeStats(int doctorId) { return statsDAO.computeStatsForDoctor(doctorId); }

    @Override
    public void refreshAndStoreStats(int doctorId) {
        DoctorStats s = statsDAO.computeStatsForDoctor(doctorId);
        statsDAO.upsertStats(s);
    }
}
