package com.hospital.management.medical.service;

import com.hospital.management.medical.dao.MedicalRecordDAO;
import com.hospital.management.medical.dao.MedicalRecordDAOImpl;
import com.hospital.management.medical.model.MedicalRecord;
import java.util.List;

public class MedicalRecordServiceImpl implements MedicalRecordService {
    private final MedicalRecordDAO dao=new MedicalRecordDAOImpl();
    @Override public int create(MedicalRecord r){return dao.create(r);}
    @Override public MedicalRecord get(int id){return dao.getById(id);}
    @Override public List<MedicalRecord> getByPatient(int patientId){return dao.getByPatient(patientId);}
    @Override public List<MedicalRecord> getAll(){return dao.getAll();}
    @Override public void update(MedicalRecord r){dao.update(r);}
    @Override public void delete(int id){dao.delete(id);}
}
