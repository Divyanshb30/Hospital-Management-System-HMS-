package com.hospital.management.dao.interfaces;



import com.hospital.management.models.Doctor;
import java.util.List;

public interface DoctorDAO {
    Doctor getDoctorById(int id);
    List<Doctor> getAllDoctors();
    boolean createDoctor(Doctor doctor);
    boolean updateDoctor(Doctor doctor);
    boolean deleteDoctor(int id);
}
