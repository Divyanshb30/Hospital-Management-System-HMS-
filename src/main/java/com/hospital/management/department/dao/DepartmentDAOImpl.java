package com.hospital.management.department.dao;

import com.hospital.management.department.model.Department;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of DepartmentDAO.
 * Replace getConnection() with your ConnectionFactory when available.
 */
public class DepartmentDAOImpl implements DepartmentDAO {

    // --- Connection wiring (swap to ConnectionFactory later) ---
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/hospital_db",
                "root",
                "password"
        );
    }

    // --- CRUD ---

    @Override
    public Long insert(Department dpt) {
        String sql = "INSERT INTO departments " +
                "(name, description, type, capacity, head_doctor_id, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        LocalDateTime now = LocalDateTime.now();
        if (dpt.getCreatedAt() == null) dpt.setCreatedAt(now);
        if (dpt.getUpdatedAt() == null) dpt.setUpdatedAt(now);

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, dpt.getName());
            ps.setString(2, dpt.getDescription());
            ps.setString(3, dpt.getType());
            ps.setInt(4, dpt.getCapacity());
            if (dpt.getHeadDoctorId() != null) ps.setLong(5, dpt.getHeadDoctorId()); else ps.setNull(5, Types.BIGINT);
            ps.setTimestamp(6, Timestamp.valueOf(dpt.getCreatedAt()));
            ps.setTimestamp(7, Timestamp.valueOf(dpt.getUpdatedAt()));

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Optional<Department> findById(Long id) {
        String sql = "SELECT * FROM departments WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Department> findAll() {
        String sql = "SELECT * FROM departments ORDER BY name ASC";
        List<Department> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Department> searchByName(String nameQuery) {
        String sql = "SELECT * FROM departments WHERE LOWER(name) LIKE ? ORDER BY name ASC";
        List<Department> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String like = "%" + (nameQuery == null ? "" : nameQuery.toLowerCase()) + "%";
            ps.setString(1, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Department> findByType(String type) {
        String sql = "SELECT * FROM departments WHERE type = ? ORDER BY name ASC";
        List<Department> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, type);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Department> findByHeadDoctorId(Long headDoctorId) {
        String sql = "SELECT * FROM departments WHERE head_doctor_id = ? ORDER BY name ASC";
        List<Department> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, headDoctorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean update(Department dpt) {
        String sql = "UPDATE departments SET name=?, description=?, type=?, capacity=?, " +
                "head_doctor_id=?, updated_at=? WHERE id=?";
        LocalDateTime now = LocalDateTime.now();
        dpt.setUpdatedAt(now);

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dpt.getName());
            ps.setString(2, dpt.getDescription());
            ps.setString(3, dpt.getType());
            ps.setInt(4, dpt.getCapacity());
            if (dpt.getHeadDoctorId() != null) ps.setLong(5, dpt.getHeadDoctorId()); else ps.setNull(5, Types.BIGINT);
            ps.setTimestamp(6, Timestamp.valueOf(dpt.getUpdatedAt()));
            ps.setLong(7, dpt.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateCapacity(Long id, int newCapacity) {
        String sql = "UPDATE departments SET capacity=?, updated_at=? WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, newCapacity);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(3, id);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM departments WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM departments";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    // --- Row mapper ---
    private Department mapRow(ResultSet rs) throws SQLException {
        Department d = new Department();
        d.setId(rs.getLong("id"));
        d.setName(rs.getString("name"));
        d.setDescription(rs.getString("description"));
        d.setType(rs.getString("type"));
        d.setCapacity(rs.getInt("capacity"));

        long headId = rs.getLong("head_doctor_id");
        d.setHeadDoctorId(rs.wasNull() ? null : headId);

        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) d.setCreatedAt(created.toLocalDateTime());

        Timestamp updated = rs.getTimestamp("updated_at");
        if (updated != null) d.setUpdatedAt(updated.toLocalDateTime());

        return d;
    }
}
