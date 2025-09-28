package com.hospital.management.doctor.dao;

import com.hospital.management.doctor.model.Doctor;
import java.util.List;

public interface DoctorDAO {
    int addDoctor(Doctor doctor);
    Doctor getDoctorById(int id);
    List<Doctor> getAllDoctors();
    void updateDoctor(Doctor doctor);
    void deleteDoctor(int id);
}
