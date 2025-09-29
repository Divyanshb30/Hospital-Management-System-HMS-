package com.hospital.management.doctor.service;

import com.hospital.management.doctor.model.*;

import java.util.List;

public interface DoctorService {
    // Doctor CRUD
    int createDoctor(Doctor doctor);
    Doctor getDoctor(int id);
    List<Doctor> getAllDoctors();
    void updateDoctor(Doctor doctor);
    void deleteDoctor(int id);

    // Specialization management
    int addSpecialization(String name, String description);
    List<DoctorSpecialization> listSpecializations();
    boolean deleteSpecialization(int id);

    // Schedule management
    int addSchedule(DoctorSchedule schedule);
    List<DoctorSchedule> getSchedulesForDoctor(int doctorId);
    void deleteSchedule(int scheduleId);

    // Stats
    DoctorStats computeStats(int doctorId);
    void refreshAndStoreStats(int doctorId);
}
