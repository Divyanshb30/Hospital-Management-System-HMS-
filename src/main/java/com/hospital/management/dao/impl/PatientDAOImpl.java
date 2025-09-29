package com.hospital.management.dao.impl;


import com.hospital.management.dao.interfaces.PatientDAO;
import com.hospital.management.models.Patient;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAOImpl implements PatientDAO {

    @Override
    public Patient getPatientById(int id) {
        String sql = "SELECT * FROM patients WHERE id = ?";
        try (Connection conn = com.hospital.management.common.config.DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToPatient(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients";
        try (Connection conn = com.hospital.management.common.config.DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                patients.add(mapResultSetToPatient(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patients;
    }

    @Override
    public boolean createPatient(Patient patient) {
        String sql = "INSERT INTO patients (user_id, first_name, last_name, date_of_birth, gender, blood_group, address, emergency_contact_name, emergency_contact_phone, insurance_number, medical_history, allergies) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = com.hospital.management.common.config.DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, patient.getId()); // assuming patient.getId() is user_id ?
            stmt.setString(2, patient.getFirstName());
            stmt.setString(3, patient.getLastName());
            stmt.setDate(4, Date.valueOf(patient.getDateOfBirth()));
            stmt.setString(5, patient.getGender().name());
            stmt.setString(6, patient.getBloodGroup());
            stmt.setString(7, patient.getAddress());
            stmt.setString(8, patient.getEmergencyContactName());
            stmt.setString(9, patient.getEmergencyContactPhone());
            stmt.setString(10, patient.getInsuranceNumber());
            stmt.setString(11, patient.getMedicalHistory());
            stmt.setString(12, patient.getAllergies());
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updatePatient(Patient patient) {
        String sql = "UPDATE patients SET first_name=?, last_name=?, date_of_birth=?, gender=?, blood_group=?, address=?, emergency_contact_name=?, emergency_contact_phone=?, insurance_number=?, medical_history=?, allergies=? WHERE id=?";
        try (Connection conn = com.hospital.management.common.config.DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, patient.getFirstName());
            stmt.setString(2, patient.getLastName());
            stmt.setDate(3, Date.valueOf(patient.getDateOfBirth()));
            stmt.setString(4, patient.getGender().name());
            stmt.setString(5, patient.getBloodGroup());
            stmt.setString(6, patient.getAddress());
            stmt.setString(7, patient.getEmergencyContactName());
            stmt.setString(8, patient.getEmergencyContactPhone());
            stmt.setString(9, patient.getInsuranceNumber());
            stmt.setString(10, patient.getMedicalHistory());
            stmt.setString(11, patient.getAllergies());
            stmt.setLong(12, patient.getId());
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deletePatient(int id) {
        String sql = "DELETE FROM patients WHERE id = ?";
        try (Connection conn = com.hospital.management.common.config.DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Patient mapResultSetToPatient(ResultSet rs) throws SQLException {
        Patient patient = new Patient();
        patient.setId(rs.getLong("id"));
        patient.setFirstName(rs.getString("first_name"));
        patient.setLastName(rs.getString("last_name"));
        patient.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
        patient.setGender(Patient.Gender.valueOf(rs.getString("gender")));
        patient.setBloodGroup(rs.getString("blood_group"));
        patient.setAddress(rs.getString("address"));
        patient.setEmergencyContactName(rs.getString("emergency_contact_name"));
        patient.setEmergencyContactPhone(rs.getString("emergency_contact_phone"));
        patient.setInsuranceNumber(rs.getString("insurance_number"));
        patient.setMedicalHistory(rs.getString("medical_history"));
        patient.setAllergies(rs.getString("allergies"));
        // TODO: Fill user details if needed
        return patient;
    }
}
