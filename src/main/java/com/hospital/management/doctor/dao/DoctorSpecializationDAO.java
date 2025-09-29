package com.hospital.management.doctor.dao;

import com.hospital.management.doctor.model.DoctorSpecialization;

import java.util.List;

public interface DoctorSpecializationDAO {
    int addSpecialization(DoctorSpecialization s);
    DoctorSpecialization getById(int id);
    List<DoctorSpecialization> getAll();
    boolean delete(int id);
}
