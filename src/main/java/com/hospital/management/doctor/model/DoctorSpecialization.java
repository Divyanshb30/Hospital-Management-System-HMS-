package com.hospital.management.doctor.model;

public class DoctorSpecialization {
    private Integer specializationId;
    private String name;
    private String description;

    // Constructors
    public DoctorSpecialization() {}

    public DoctorSpecialization(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters and Setters
    public Integer getSpecializationId() { return specializationId; }
    public void setSpecializationId(Integer specializationId) { this.specializationId = specializationId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return String.format("Specialization{id=%d, name='%s', description='%s'}",
                specializationId, name, description);
    }
}
