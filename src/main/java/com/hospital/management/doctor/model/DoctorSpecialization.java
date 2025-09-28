package com.hospital.management.doctor.model;

public class DoctorSpecialization {
    private int specializationId;
    private String name;
    private String description;

    public DoctorSpecialization() {}
    public DoctorSpecialization(String name, String description){
        this.name = name;
        this.description = description;
    }


    public int getSpecializationId() {
        return this.specializationId ;
    }

    public void setSpecializationId(int specializationId) {
        this.specializationId = specializationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public String toString(){ return specializationId + " | " + name + " | " + description; }
}
