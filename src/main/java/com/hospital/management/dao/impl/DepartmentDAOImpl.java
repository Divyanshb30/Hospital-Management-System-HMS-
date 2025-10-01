package com.hospital.management.dao.impl;

import com.hospital.management.dao.interfaces.DepartmentDAO;
import com.hospital.management.models.Department;
import com.hospital.management.common.config.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAOImpl implements DepartmentDAO {

    @Override
    public Department getDepartmentById(Long id) {
        String sql = "SELECT * FROM departments WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToDepartment(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Department> getAllDepartments() {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT * FROM departments ORDER BY name";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                departments.add(mapResultSetToDepartment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return departments;
    }

    @Override
    public List<Department> getActiveDepartments() {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT * FROM departments WHERE is_active = true ORDER BY name";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                departments.add(mapResultSetToDepartment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return departments;
    }

    @Override
    public boolean createDepartment(Department department) {
        String sql = "INSERT INTO departments (name, description, head_doctor_id, location, phone, is_active, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, department.getName());
            stmt.setString(2, department.getDescription());
            stmt.setObject(3, department.getHeadDoctorId());
            stmt.setString(4, department.getLocation());
            stmt.setString(5, department.getPhone());
            stmt.setBoolean(6, department.isActive());
            stmt.setTimestamp(7, Timestamp.valueOf(department.getCreatedAt()));
            stmt.setTimestamp(8, Timestamp.valueOf(department.getUpdatedAt()));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateDepartment(Department department) {
        String sql = "UPDATE departments SET name = ?, description = ?, head_doctor_id = ?, location = ?, phone = ?, is_active = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, department.getName());
            stmt.setString(2, department.getDescription());
            stmt.setObject(3, department.getHeadDoctorId());
            stmt.setString(4, department.getLocation());
            stmt.setString(5, department.getPhone());
            stmt.setBoolean(6, department.isActive());
            stmt.setTimestamp(7, Timestamp.valueOf(department.getUpdatedAt()));
            stmt.setLong(8, department.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteDepartment(Long id) {
        String sql = "DELETE FROM departments WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Department getDepartmentByName(String name) {
        String sql = "SELECT * FROM departments WHERE name = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToDepartment(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Department> getDepartmentsWithDoctors() {
        List<Department> departments = new ArrayList<>();
        String sql = """
        SELECT DISTINCT d.* FROM departments d 
        JOIN doctors dr ON d.id = dr.department_id 
        WHERE d.is_active = true AND dr.is_available = true 
        ORDER BY d.name
        """;
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                departments.add(mapResultSetToDepartment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return departments;
    }



    private Department mapResultSetToDepartment(ResultSet rs) throws SQLException {
        Department department = new Department();
        department.setId(rs.getLong("id"));
        department.setName(rs.getString("name"));
        department.setDescription(rs.getString("description"));
        Long headDoctorId = rs.getObject("head_doctor_id", Long.class);
        department.setHeadDoctorId(headDoctorId);
        department.setLocation(rs.getString("location"));
        department.setPhone(rs.getString("phone"));
        department.setActive(rs.getBoolean("is_active"));
        department.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        department.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return department;
    }
}
