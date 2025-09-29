package com.hospital.management.doctor.dao;

import com.hospital.management.common.config.DatabaseConfig;
import com.hospital.management.doctor.model.Doctor;
import com.hospital.management.doctor.model.DoctorSpecialization;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorSpecializationDAOImpl implements DoctorSpecializationDAO {

    @Override
    public int addSpecialization(DoctorSpecialization s) {
        String sql = "INSERT INTO specializations (name, description) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getDescription());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    s.setSpecializationId(rs.getInt(1));
                    return s.getSpecializationId();
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    @Override
    public DoctorSpecialization getById(int id) {
        String sql = "SELECT * FROM specializations WHERE specialization_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public List<DoctorSpecialization> getAll() {
        List<DoctorSpecialization> list = new ArrayList<>();
        String sql = "SELECT * FROM specializations";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM specializations WHERE specialization_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }


    private DoctorSpecialization mapRow(ResultSet rs) throws SQLException {
        DoctorSpecialization s = new DoctorSpecialization();
        s.setSpecializationId(rs.getInt("specialization_id"));
        s.setName(rs.getString("name"));
        s.setDescription(rs.getString("description"));
        return s;
    }
}
