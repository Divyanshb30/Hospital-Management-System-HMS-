package com.hospital.management.medical.dao;

import com.hospital.management.common.config.DatabaseConfig;
import com.hospital.management.medical.model.MedicalRecord;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MedicalRecordDAOImpl implements MedicalRecordDAO {
    @Override
    public int create(MedicalRecord r) {
        String sql = "INSERT INTO medical_records (patient_id, created_by_doctor_id, chief_complaint, notes) VALUES (?,?,?,?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getPatientId());
            if (r.getCreatedByDoctorId()==null) ps.setNull(2, Types.INTEGER); else ps.setInt(2, r.getCreatedByDoctorId());
            ps.setString(3, r.getChiefComplaint());
            ps.setString(4, r.getNotes());
            ps.executeUpdate();
            try(ResultSet rs=ps.getGeneratedKeys()){if(rs.next()){r.setMedicalRecordId(rs.getInt(1));return r.getMedicalRecordId();}}
        }catch(Exception e){e.printStackTrace();}
        return -1;
    }
    @Override public MedicalRecord getById(int id){
        try(Connection c=DatabaseConfig.getConnection();
            PreparedStatement ps=c.prepareStatement("SELECT * FROM medical_records WHERE medical_record_id=?")){
            ps.setInt(1,id);try(ResultSet rs=ps.executeQuery()){if(rs.next())return map(rs);}}
        catch(Exception e){e.printStackTrace();}
        return null;
    }
    @Override public List<MedicalRecord> getByPatient(int patientId){
        List<MedicalRecord>list=new ArrayList<>();
        try(Connection c=DatabaseConfig.getConnection();
            PreparedStatement ps=c.prepareStatement("SELECT * FROM medical_records WHERE patient_id=?")){
            ps.setInt(1,patientId);try(ResultSet rs=ps.executeQuery()){while(rs.next())list.add(map(rs));}}
        catch(Exception e){e.printStackTrace();}
        return list;
    }
    @Override public List<MedicalRecord> getAll(){
        List<MedicalRecord>list=new ArrayList<>();
        try(Connection c=DatabaseConfig.getConnection();
            Statement st=c.createStatement();ResultSet rs=st.executeQuery("SELECT * FROM medical_records")){
            while(rs.next())list.add(map(rs));
        }catch(Exception e){e.printStackTrace();}
        return list;
    }
    @Override public void update(MedicalRecord r){
        try(Connection c=DatabaseConfig.getConnection();
            PreparedStatement ps=c.prepareStatement("UPDATE medical_records SET chief_complaint=?,notes=?,created_by_doctor_id=? WHERE medical_record_id=?")){
            ps.setString(1,r.getChiefComplaint());
            ps.setString(2,r.getNotes());
            if(r.getCreatedByDoctorId()==null)ps.setNull(3,Types.INTEGER);else ps.setInt(3,r.getCreatedByDoctorId());
            ps.setInt(4,r.getMedicalRecordId());
            ps.executeUpdate();
        }catch(Exception e){e.printStackTrace();}
    }
    @Override public void delete(int id){
        try(Connection c=DatabaseConfig.getConnection();
            PreparedStatement ps=c.prepareStatement("DELETE FROM medical_records WHERE medical_record_id=?")){
            ps.setInt(1,id);ps.executeUpdate();
        }catch(Exception e){e.printStackTrace();}
    }
    private MedicalRecord map(ResultSet rs)throws SQLException{
        MedicalRecord r=new MedicalRecord();
        r.setMedicalRecordId(rs.getInt("medical_record_id"));
        r.setPatientId(rs.getInt("patient_id"));
        int did=rs.getInt("created_by_doctor_id");
        if(rs.wasNull())r.setCreatedByDoctorId(null);else r.setCreatedByDoctorId(did);
        r.setChiefComplaint(rs.getString("chief_complaint"));
        r.setNotes(rs.getString("notes"));
        Timestamp cts=rs.getTimestamp("created_at");
        Timestamp uts=rs.getTimestamp("updated_at");
        if(cts!=null)r.setCreatedAt(cts.toLocalDateTime());
        if(uts!=null)r.setUpdatedAt(uts.toLocalDateTime());
        return r;
    }
}
