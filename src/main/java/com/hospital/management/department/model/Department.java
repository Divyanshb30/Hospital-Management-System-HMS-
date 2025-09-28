package com.hospital.management.department.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Department entity
 * -----------------
 * Represents a hospital department (e.g., Cardiology, Neurology).
 * Each department has an ID, name, type, capacity, head of department,
 * and timestamps for audit.
 */
public class Department {

    private Long id;                     // Unique identifier
    private String name;                 // Human-readable name ("Cardiology")
    private String description;          // Optional notes about the department
    private String type;                 // Enum-like field (could map to DepartmentType enum)
    private int capacity;                // Number of beds/slots available
    private Long headDoctorId;           // Doctor responsible (foreign key)
    private LocalDateTime createdAt;     // Audit timestamp
    private LocalDateTime updatedAt;     // Audit timestamp

    // --- Constructors ---
    public Department() {}

    public Department(Long id, String name, String description, String type,
                      int capacity, Long headDoctorId,
                      LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.capacity = capacity;
        this.headDoctorId = headDoctorId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public Long getHeadDoctorId() { return headDoctorId; }
    public void setHeadDoctorId(Long headDoctorId) { this.headDoctorId = headDoctorId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // --- Utility methods ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Department)) return false;
        Department that = (Department) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Department{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", capacity=" + capacity +
                ", headDoctorId=" + headDoctorId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
