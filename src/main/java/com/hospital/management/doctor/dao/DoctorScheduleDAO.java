package com.hospital.management.doctor.dao;

import com.hospital.management.doctor.model.DoctorSchedule;

import java.util.List;

public interface DoctorScheduleDAO {
    int addSchedule(DoctorSchedule schedule);
    List<DoctorSchedule> getSchedulesForDoctor(int doctorId);
    void deleteSchedule(int scheduleId);
}
