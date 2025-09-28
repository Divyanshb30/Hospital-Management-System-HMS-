package com.hospital.management.appointment.dao;

import com.hospital.management.appointment.model.Appointment;
import com.hospital.management.appointment.model.AppointmentConflict;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of AppointmentDAO.
 * Uses try-with-resources for safe resource management.
 */
public class AppointmentDAOImpl implements AppointmentDAO {

    private Connection getConnection() throws SQLException {
        // Replace with ConnectionFactory later
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/hospital_db",
                "root",
                "password"
        );
    }

    @Override
    public Long insert(Appointment appointment) {
        String sql = "INSERT INTO appointments (patient_id, doctor_id, appointment_time, status, notes) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, appointment.getPatientId());
            stmt.setLong(2, appointment.getDoctorId());
            stmt.setTimestamp(3, Timestamp.valueOf(appointment.getAppointmentTime()));
            stmt.setString(4, appointment.getStatus());
            stmt.setString(5, appointment.getNotes());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Optional<Appointment> findById(Long id) {
        String sql = "SELECT * FROM appointments WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Appointment> findAll() {
        String sql = "SELECT * FROM appointments";
        List<Appointment> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Appointment> findByPatientId(Long patientId) {
        String sql = "SELECT * FROM appointments WHERE patient_id = ?";
        List<Appointment> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, patientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Appointment> findByDoctorId(Long doctorId) {
        String sql = "SELECT * FROM appointments WHERE doctor_id = ?";
        List<Appointment> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, doctorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Appointment> findByDoctorAndTimeRange(Long doctorId, LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT * FROM appointments WHERE doctor_id = ? AND appointment_time BETWEEN ? AND ?";
        List<Appointment> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, doctorId);
            stmt.setTimestamp(2, Timestamp.valueOf(start));
            stmt.setTimestamp(3, Timestamp.valueOf(end));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Appointment> findByStatus(String status) {
        String sql = "SELECT * FROM appointments WHERE status = ?";
        List<Appointment> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean update(Appointment appointment) {
        String sql = "UPDATE appointments SET patient_id=?, doctor_id=?, appointment_time=?, status=?, notes=? WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, appointment.getPatientId());
            stmt.setLong(2, appointment.getDoctorId());
            stmt.setTimestamp(3, Timestamp.valueOf(appointment.getAppointmentTime()));
            stmt.setString(4, appointment.getStatus());
            stmt.setString(5, appointment.getNotes());
            stmt.setLong(6, appointment.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM appointments WHERE id = ?";
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
        String sql = "SELECT COUNT(*) FROM appointments";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<AppointmentConflict> checkConflicts(Long doctorId, LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT id, patient_id, doctor_id, appointment_time, status, notes " +
                "FROM appointments " +
                "WHERE doctor_id = ? AND appointment_time BETWEEN ? AND ?";

        List<AppointmentConflict> conflicts = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, doctorId);
            stmt.setTimestamp(2, Timestamp.valueOf(start));
            stmt.setTimestamp(3, Timestamp.valueOf(end));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Long existingId = rs.getLong("id");
                    LocalDateTime existingTime = rs.getTimestamp("appointment_time").toLocalDateTime();
                    if (AppointmentConflict.isOverlapping(start, end, existingTime, existingTime.plusMinutes(30))) {
                        AppointmentConflict conflict = new AppointmentConflict();
                        conflict.setAppointmentId1(existingId);
                        conflict.setAppointmentId2(null); // new one not persisted yet
                        conflict.setDoctorId(doctorId);
                        conflict.setConflictStart(existingTime);
                        conflict.setConflictEnd(existingTime.plusMinutes(30));
                        conflict.setReason("Overlapping slots");
                        conflicts.add(conflict);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conflicts;
    }

    // --- Mapper ---
    private Appointment mapRow(ResultSet rs) throws SQLException {
        Appointment appt = new Appointment();
        appt.setId(rs.getLong("id"));
        appt.setPatientId(rs.getLong("patient_id"));
        appt.setDoctorId(rs.getLong("doctor_id"));
        appt.setAppointmentTime(rs.getTimestamp("appointment_time").toLocalDateTime());
        appt.setStatus(rs.getString("status"));
        appt.setNotes(rs.getString("notes"));
        return appt;
    }
}
