package com.hospital.management.doctor.model;

import java.util.List;

public class Doctor {
    private int doctorId;
    private String name;
    private Integer specializationId; // nullable
    private String contact;
    private String department;

    public Doctor() {}

    public Doctor(String name, Integer specializationId, String contact, String department) {
        this.name = name;
        this.specializationId = specializationId;
        this.contact = contact;
        this.department = department;
    }

    // Getters & Setters
    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Integer getSpecializationId() {
        return specializationId;
    }

    public void setSpecializationId(Integer specializationId) {
        this.specializationId = specializationId;
    }

    // toString() for CLI printing
    @Override
    public String toString() {
        return String.format("%d | %s | specId=%s | %s | %s",
                doctorId, name, specializationId == null ? "N/A" : specializationId, contact, department);
    }
}
