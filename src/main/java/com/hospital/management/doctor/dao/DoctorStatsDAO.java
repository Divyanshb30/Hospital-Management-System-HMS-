package com.hospital.management.doctor.dao;

import com.hospital.management.doctor.model.DoctorStats;

public interface DoctorStatsDAO {
    DoctorStats computeStatsForDoctor(int doctorId); // aggregates from appointments
    void upsertStats(DoctorStats stats); // optional store
}
