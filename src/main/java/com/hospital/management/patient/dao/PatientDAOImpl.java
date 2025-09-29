package com.hospital.management.patient.dao;

import com.hospital.management.common.config.DatabaseConfig; // Add this import
import com.hospital.management.patient.model.Patient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
/**
 * JDBC implementation of PatientDAO using try-with-resources.
 */
public class PatientDAOImpl implements PatientDAO {

    // You will later replace this with ConnectionFactory
    // ✅ FIXED: Use our DatabaseConfig instead of hardcoded connection
    private Connection getConnection() throws SQLException {
        return DatabaseConfig.getConnection();
    }

    // ... rest of your existing methods remain exactly the same ...
    @Override
    public Long insert(Patient patient) {
        String sql = "INSERT INTO patients (first_name, last_name, date_of_birth, gender, phone, email, address, blood_group, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, patient.getFirstName());
            stmt.setString(2, patient.getLastName());
            stmt.setDate(3, patient.getDateOfBirth() != null ? Date.valueOf(patient.getDateOfBirth()) : null);
            stmt.setString(4, patient.getGender());
            stmt.setString(5, patient.getPhone());
            stmt.setString(6, patient.getEmail());
            stmt.setString(7, patient.getAddress());
            stmt.setString(8, patient.getBloodGroup());
            stmt.setString(9, patient.getStatus());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error inserting patient: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Optional<Patient> findById(Long id) {
        String sql = "SELECT * FROM patients WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Patient> findAll() {
        String sql = "SELECT * FROM patients";
        List<Patient> patients = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                patients.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patients;
    }

    @Override
    public List<Patient> search(String query) {
        String sql = "SELECT * FROM patients WHERE LOWER(first_name) LIKE ? OR LOWER(last_name) LIKE ? OR phone LIKE ? OR email LIKE ?";
        List<Patient> patients = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String likeQuery = "%" + query.toLowerCase() + "%";
            stmt.setString(1, likeQuery);
            stmt.setString(2, likeQuery);
            stmt.setString(3, likeQuery);
            stmt.setString(4, likeQuery);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    patients.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patients;
    }

    @Override
    public boolean update(Patient patient) {
        String sql = "UPDATE patients SET first_name=?, last_name=?, date_of_birth=?, gender=?, phone=?, email=?, address=?, blood_group=?, status=? WHERE id=?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, patient.getFirstName());
            stmt.setString(2, patient.getLastName());
            stmt.setDate(3, patient.getDateOfBirth() != null ? Date.valueOf(patient.getDateOfBirth()) : null);
            stmt.setString(4, patient.getGender());
            stmt.setString(5, patient.getPhone());
            stmt.setString(6, patient.getEmail());
            stmt.setString(7, patient.getAddress());
            stmt.setString(8, patient.getBloodGroup());
            stmt.setString(9, patient.getStatus());
            stmt.setLong(10, patient.getId());

            return stmt.executeUpdate() >= 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM patients WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM patients";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getLong(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // --- Utility to map DB row -> Patient object ---
    private Patient mapRow(ResultSet rs) throws SQLException {
        Patient patient = new Patient();
        patient.setId(rs.getLong("id"));
        patient.setFirstName(rs.getString("first_name"));
        patient.setLastName(rs.getString("last_name"));

        Date dob = rs.getDate("date_of_birth");
        if (dob != null) patient.setDateOfBirth(dob.toLocalDate());

        patient.setGender(rs.getString("gender"));
        patient.setPhone(rs.getString("phone"));
        patient.setEmail(rs.getString("email"));
        patient.setAddress(rs.getString("address"));
        patient.setBloodGroup(rs.getString("blood_group"));
        patient.setStatus(rs.getString("status"));

        return patient;
    }
}