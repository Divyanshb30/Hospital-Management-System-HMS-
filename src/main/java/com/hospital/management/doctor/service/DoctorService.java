package com.hospital.management.doctor.service;

import com.hospital.management.doctor.model.Doctor;
import com.hospital.management.doctor.model.DoctorSchedule;
import com.hospital.management.doctor.model.DoctorSpecialization;
import com.hospital.management.doctor.model.DoctorStats;
import java.util.List;

public interface DoctorService {
    int createDoctor(Doctor doctor);
    Doctor getDoctor(int id);
    List<Doctor> getAllDoctors();
    void updateDoctor(Doctor doctor);
    void deleteDoctor(int id);

    // specializations
    int addSpecialization(String name, String description);
    List<DoctorSpecialization> listSpecializations();
    void deleteSpecialization(int id);


    // schedules
    int addSchedule(DoctorSchedule scheduleObj);
    List<DoctorSchedule> getSchedulesForDoctor(int doctorId);
    void deleteSchedule(int scheduleId);

    // stats
    DoctorStats computeStats(int doctorId);
    void refreshAndStoreStats(int doctorId);

}
