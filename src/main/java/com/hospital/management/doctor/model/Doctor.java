package com.hospital.management.doctor.model;

import java.time.LocalDateTime;

public class Doctor {
    private Integer doctorId;
    private String name;
    private Integer specializationId;
    private String contact;
    private String department;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public Doctor() {}

    public Doctor(String name, Integer specializationId, String contact, String department) {
        this.name = name;
        this.specializationId = specializationId;
        this.contact = contact;
        this.department = department;
    }

    // Getters and Setters
    public Integer getDoctorId() { return doctorId; }
    public void setDoctorId(Integer doctorId) { this.doctorId = doctorId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getSpecializationId() { return specializationId; }
    public void setSpecializationId(Integer specializationId) { this.specializationId = specializationId; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return String.format("Doctor{id=%d, name='%s', specialization=%d, contact='%s', department='%s'}",
                doctorId, name, specializationId, contact, department);
    }
}
