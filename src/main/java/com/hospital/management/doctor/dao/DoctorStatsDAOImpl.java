package com.hospital.management.doctor.dao;

import com.hospital.management.common.config.DatabaseConfig;
import com.hospital.management.doctor.model.DoctorStats;

import java.sql.*;

public class DoctorStatsDAOImpl implements DoctorStatsDAO{
    @Override
    public DoctorStats computeStatsForDoctor(int doctorId) {
        String totalSql = "SELECT COUNT(*) AS total, SUM(CASE WHEN status='COMPLETED' THEN 1 ELSE 0 END) AS completed FROM appointments WHERE doctor_id=?";
        DoctorStats stats = new DoctorStats();
        stats.setDoctorId(doctorId);
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(totalSql)) {
            ps.setInt(1, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.setTotalAppointments(rs.getInt("total"));
                    stats.setCompletedAppointments(rs.getInt("completed"));
                }
            }
            // average_rating is app-dependent; left 0.0 unless you have ratings table
            stats.setAverageRating(0.0);
        } catch (SQLException e) { e.printStackTrace(); }
        return stats;
    }

    @Override
    public void upsertStats(DoctorStats stats) {
        String upsert = "INSERT INTO doctor_stats (doctor_id, total_appointments, completed_appointments, average_rating) " +
                "VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE total_appointments=VALUES(total_appointments), completed_appointments=VALUES(completed_appointments), average_rating=VALUES(average_rating)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(upsert)) {
            ps.setInt(1, stats.getDoctorId());
            ps.setInt(2, stats.getTotalAppointments());
            ps.setInt(3, stats.getCompletedAppointments());
            ps.setDouble(4, stats.getAverageRating());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
