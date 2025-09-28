package com.hospital.management.doctor.model;

import java.time.LocalTime;

public class DoctorSchedule {
    private int scheduleId;
    private int doctorId;
    private String dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    public DoctorSchedule() {}

    public DoctorSchedule(int doctorId, String dayOfWeek, LocalTime startTime, LocalTime endTime) {
        this.doctorId = doctorId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    // getters/setters


    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return String.format("%d | doctor:%d | %s | %s - %s",
                scheduleId, doctorId, dayOfWeek, startTime, endTime);
    }
}
