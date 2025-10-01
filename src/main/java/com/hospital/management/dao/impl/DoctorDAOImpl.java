package com.hospital.management.dao.impl;


import com.hospital.management.dao.interfaces.DoctorDAO;
import com.hospital.management.models.Doctor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAOImpl implements DoctorDAO {

    @Override
    public Doctor getDoctorById(int id) {
        String sql = "SELECT * FROM doctors WHERE id = ?";
        try (Connection conn = com.hospital.management.common.config.DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToDoctor(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors";
        try (Connection conn = com.hospital.management.common.config.DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                doctors.add(mapResultSetToDoctor(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return doctors;
    }

    @Override
    public boolean createDoctor(Doctor doctor) {
        String sql = "INSERT INTO doctors (user_id, first_name, last_name, specialization, license_number, department_id, qualification, experience_years, consultation_fee, available_from, available_to, is_available) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = com.hospital.management.common.config.DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, doctor.getId());  // assuming doctor.getId() is user_id ?
            stmt.setString(2, doctor.getFirstName());
            stmt.setString(3, doctor.getLastName());
            stmt.setString(4, doctor.getSpecialization());
            stmt.setString(5, doctor.getLicenseNumber());
            stmt.setLong(6, doctor.getDepartmentId());
            stmt.setString(7, doctor.getQualification());
            stmt.setInt(8, doctor.getExperienceYears());
            stmt.setBigDecimal(9, doctor.getConsultationFee());
            stmt.setTime(10, Time.valueOf(doctor.getAvailableFrom()));
            stmt.setTime(11, Time.valueOf(doctor.getAvailableTo()));
            stmt.setBoolean(12, doctor.isDoctorAvailable());
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateDoctor(Doctor doctor) {
        String sql = "UPDATE doctors SET first_name=?, last_name=?, specialization=?, license_number=?, department_id=?, qualification=?, experience_years=?, consultation_fee=?, available_from=?, available_to=?, is_available=? WHERE id=?";
        try (Connection conn = com.hospital.management.common.config.DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, doctor.getFirstName());
            stmt.setString(2, doctor.getLastName());
            stmt.setString(3, doctor.getSpecialization());
            stmt.setString(4, doctor.getLicenseNumber());
            stmt.setLong(5, doctor.getDepartmentId());
            stmt.setString(6, doctor.getQualification());
            stmt.setInt(7, doctor.getExperienceYears());
            stmt.setBigDecimal(8, doctor.getConsultationFee());
            stmt.setTime(9, Time.valueOf(doctor.getAvailableFrom()));
            stmt.setTime(10, Time.valueOf(doctor.getAvailableTo()));
            stmt.setBoolean(11, doctor.isDoctorAvailable());
            stmt.setLong(12, doctor.getId());
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteDoctor(int id) {
        String sql = "DELETE FROM doctors WHERE id = ?";
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

    @Override
    public List<Doctor> getDoctorsByDepartment(Long departmentId) {
        List<Doctor> doctors = new ArrayList<>();
        String sql = """
        SELECT d.*, u.username, u.email, u.phone 
        FROM doctors d 
        JOIN users u ON d.user_id = u.id 
        WHERE d.department_id = ? AND d.is_available = true AND u.is_active = true
        ORDER BY d.first_name, d.last_name
        """;
        try (Connection conn = com.hospital.management.common.config.DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, departmentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Doctor doctor = mapResultSetToDoctor(rs);
                doctor.setUsername(rs.getString("username"));
                doctor.setEmail(rs.getString("email"));
                doctor.setPhone(rs.getString("phone"));
                doctors.add(doctor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return doctors;
    }

    @Override
    public boolean updateDoctorQualification(Long doctorId, String qualification) {
        String sql = "UPDATE doctors SET qualification=?, updated_at=NOW() WHERE id=?";

        try (Connection conn = com.hospital.management.common.config.DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, qualification);
            stmt.setLong(2, doctorId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error updating doctor qualification: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateConsultationFee(Long doctorId, java.math.BigDecimal consultationFee) {
        String sql = "UPDATE doctors SET consultation_fee=?, updated_at=NOW() WHERE id=?";

        try (Connection conn = com.hospital.management.common.config.DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, consultationFee);
            stmt.setLong(2, doctorId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error updating consultation fee: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }



    private Doctor mapResultSetToDoctor(ResultSet rs) throws SQLException {
        Doctor doctor = new Doctor();
        doctor.setId(rs.getLong("id"));
        doctor.setFirstName(rs.getString("first_name"));
        doctor.setLastName(rs.getString("last_name"));
        doctor.setSpecialization(rs.getString("specialization"));
        doctor.setLicenseNumber(rs.getString("license_number"));
        doctor.setDepartmentId(rs.getLong("department_id"));
        doctor.setQualification(rs.getString("qualification"));
        doctor.setExperienceYears(rs.getInt("experience_years"));
        doctor.setConsultationFee(rs.getBigDecimal("consultation_fee"));
        doctor.setAvailableFrom(rs.getTime("available_from").toLocalTime());
        doctor.setAvailableTo(rs.getTime("available_to").toLocalTime());
        doctor.setDoctorAvailable(rs.getBoolean("is_available"));
        // TODO: Fill user details if needed
        return doctor;
    }

}
