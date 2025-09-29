package com.hospital.management.medical.service;

import com.hospital.management.medical.model.MedicalRecord;
import java.util.List;

public interface MedicalRecordService {
    int create(MedicalRecord r);
    MedicalRecord get(int id);
    List<MedicalRecord> getByPatient(int patientId);
    List<MedicalRecord> getAll();
    void update(MedicalRecord r);
    void delete(int id);
}
