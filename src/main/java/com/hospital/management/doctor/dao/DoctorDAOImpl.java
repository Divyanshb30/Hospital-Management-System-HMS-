package com.hospital.management.doctor.dao;

import com.hospital.management.common.config.DatabaseConfig;
import com.hospital.management.doctor.model.Doctor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAOImpl implements DoctorDAO {

    @Override
    public int addDoctor(Doctor doctor) {
        String sql = "INSERT INTO doctors (name, specialization_id, contact, department) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, doctor.getName());
            if (doctor.getSpecializationId() == null) ps.setNull(2, Types.INTEGER);
            else ps.setInt(2, doctor.getSpecializationId());
            ps.setString(3, doctor.getContact());
            ps.setString(4, doctor.getDepartment());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    doctor.setDoctorId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public Doctor getDoctorById(int id) {
        String sql = "SELECT * FROM doctors WHERE doctor_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public List<Doctor> getAllDoctors() {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT * FROM doctors";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public void updateDoctor(Doctor doctor) {
        String sql = "UPDATE doctors SET name=?, specialization_id=?, contact=?, department=? WHERE doctor_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, doctor.getName());
            if (doctor.getSpecializationId() == null) ps.setNull(2, Types.INTEGER);
            else ps.setInt(2, doctor.getSpecializationId());
            ps.setString(3, doctor.getContact());
            ps.setString(4, doctor.getDepartment());
            ps.setInt(5, doctor.getDoctorId());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void deleteDoctor(int id) {
        String sql = "DELETE FROM doctors WHERE doctor_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private Doctor mapRow(ResultSet rs) throws SQLException {
        Doctor d = new Doctor();
        d.setDoctorId(rs.getInt("doctor_id"));
        d.setName(rs.getString("name"));
        int sid = rs.getInt("specialization_id");
        if (rs.wasNull()) d.setSpecializationId(null); else d.setSpecializationId(sid);
        d.setContact(rs.getString("contact"));
        d.setDepartment(rs.getString("department"));
        return d;
    }
}
