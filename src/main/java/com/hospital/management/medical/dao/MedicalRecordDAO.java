package com.hospital.management.medical.dao;

import com.hospital.management.medical.model.MedicalRecord;
import java.util.List;

public interface MedicalRecordDAO {
    int create(MedicalRecord record);
    MedicalRecord getById(int id);
    List<MedicalRecord> getByPatient(int patientId);
    List<MedicalRecord> getAll();
    void update(MedicalRecord record);
    void delete(int id);
}
