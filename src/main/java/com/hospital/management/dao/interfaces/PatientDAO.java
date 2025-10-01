package com.hospital.management.dao.interfaces;


import com.hospital.management.models.Patient;
import java.util.List;

public interface PatientDAO {
    Patient getPatientById(int id);
    List<Patient> getAllPatients();
    boolean createPatient(Patient patient);
    boolean deletePatient(int id);
    Patient getPatientByUserId(Long userId);
    boolean updatePatient(Patient patient);
}
