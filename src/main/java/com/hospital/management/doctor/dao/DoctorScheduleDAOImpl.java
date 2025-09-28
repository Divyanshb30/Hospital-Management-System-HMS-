package com.hospital.management.doctor.dao;

import com.hospital.management.common.config.DatabaseConfig;
import com.hospital.management.doctor.model.Doctor;
import com.hospital.management.doctor.model.DoctorSchedule;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DoctorScheduleDAOImpl implements DoctorScheduleDAO {

    @Override
    public int addSchedule(DoctorSchedule schedule) {
        String sql = "INSERT INTO doctor_schedules (doctor_id, day_of_week, start_time, end_time) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, schedule.getDoctorId());
            ps.setString(2, schedule.getDayOfWeek());
            ps.setTime(3, Time.valueOf(schedule.getStartTime()));
            ps.setTime(4, Time.valueOf(schedule.getEndTime()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    schedule.setScheduleId(rs.getInt(1));
                    return schedule.getScheduleId();
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    @Override
    public List<DoctorSchedule> getSchedulesForDoctor(int doctorId) {
        List<DoctorSchedule> list = new ArrayList<>();
        String sql = "SELECT * FROM doctor_schedules WHERE doctor_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public void deleteSchedule(int scheduleId) {
        String sql = "DELETE FROM doctor_schedules WHERE schedule_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, scheduleId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private DoctorSchedule mapRow(ResultSet rs) throws SQLException {
        DoctorSchedule s = new DoctorSchedule();
        s.setScheduleId(rs.getInt("schedule_id"));
        s.setDoctorId(rs.getInt("doctor_id"));
        s.setDayOfWeek(rs.getString("day_of_week"));
        s.setStartTime(rs.getTime("start_time").toLocalTime());
        s.setEndTime(rs.getTime("end_time").toLocalTime());
        return s;
    }
}
