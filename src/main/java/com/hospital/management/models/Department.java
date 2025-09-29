package com.hospital.management.models;

import com.hospital.management.common.exceptions.ValidationException;
import com.hospital.management.common.utils.InputValidator;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Department model representing hospital departments
 */
public class Department {

    private Long id;
    private String name;
    private String description;
    private Long headDoctorId;
    private String location;
    private String phone;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor
    public Department() {
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor with essential fields
    public Department(String name, String description, String location) {
        this();
        this.name = name;
        this.description = description;
        this.location = location;
    }

    // Full constructor
    public Department(Long id, String name, String description, Long headDoctorId,
                      String location, String phone, boolean isActive,
                      LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.headDoctorId = headDoctorId;
        this.location = location;
        this.phone = phone;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Validates the department object
     */
    public void validate() throws ValidationException {
        InputValidator.validateRequired(name, "Department Name");
        InputValidator.validateName(name, "Department Name");

        if (phone != null && !phone.trim().isEmpty()) {
            InputValidator.validatePhone(phone, "Department Phone");
        }

        if (name != null && (name.length() < 2 || name.length() > 100)) {
            throw new ValidationException("Department name must be 2-100 characters", "Name", name);
        }
    }

    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
        updateTimestamp();
    }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description;
        updateTimestamp();
    }

    public Long getHeadDoctorId() { return headDoctorId; }
    public void setHeadDoctorId(Long headDoctorId) {
        this.headDoctorId = headDoctorId;
        updateTimestamp();
    }

    public String getLocation() { return location; }
    public void setLocation(String location) {
        this.location = location;
        updateTimestamp();
    }

    public String getPhone() { return phone; }
    public void setPhone(String phone) {
        this.phone = phone;
        updateTimestamp();
    }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) {
        this.isActive = active;
        updateTimestamp();
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Business methods
    public String getDisplayName() {
        return name + (location != null ? " (" + location + ")" : "");
    }

    public boolean hasHeadDoctor() {
        return headDoctorId != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Department)) return false;
        Department that = (Department) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return String.format("Department{id=%d, name='%s', location='%s', active=%s}",
                id, name, location, isActive);
    }
}
