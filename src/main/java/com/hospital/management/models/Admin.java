package com.hospital.management.models;

import com.hospital.management.common.enums.UserRole;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Admin model extending User with admin-specific information
 */
public class Admin extends User {

    private String firstName;
    private String lastName;
    private String designation;
    private String permissions;

    // Default constructor
    public Admin() {
        super();
        this.role = UserRole.ADMIN;
        this.designation = "System Administrator";
        this.permissions = "ALL";
    }

    // Constructor with user fields
    public Admin(String username, String passwordHash, String email, String phone) {
        this();
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.phone = phone;
    }

    // Constructor with admin-specific fields
    public Admin(String username, String passwordHash, String email, String phone,
                 String firstName, String lastName, String designation) {
        this(username, passwordHash, email, phone);
        this.firstName = firstName;
        this.lastName = lastName;
        this.designation = designation;
    }

    // Full constructor
    public Admin(Long id, String username, String passwordHash, String email, String phone,
                 boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt,
                 String firstName, String lastName, String designation, String permissions) {
        super(id, username, passwordHash, email, phone, UserRole.ADMIN, isActive, createdAt, updatedAt);
        this.firstName = firstName;
        this.lastName = lastName;
        this.designation = designation;
        this.permissions = permissions;
    }

    // Business methods
    public String getFullName() {
        if (firstName == null || lastName == null) {
            return username;
        }
        return firstName + " " + lastName;
    }

    @Override
    public String getDisplayName() {
        return getFullName() + " (" + designation + ")";
    }

    public boolean isSuperAdmin() {
        return "SUPER_ADMIN".equalsIgnoreCase(designation) || "ALL".equalsIgnoreCase(permissions);
    }

    public boolean hasPermission(String permission) {
        return permissions != null &&
                (permissions.contains("ALL") || permissions.contains(permission));
    }

    // Getters and Setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
        updateTimestamp();
    }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) {
        this.lastName = lastName;
        updateTimestamp();
    }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) {
        this.designation = designation;
        updateTimestamp();
    }

    public String getPermissions() { return permissions; }
    public void setPermissions(String permissions) {
        this.permissions = permissions;
        updateTimestamp();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Admin)) return false;
        if (!super.equals(o)) return false;
        Admin admin = (Admin) o;
        return Objects.equals(firstName, admin.firstName) &&
                Objects.equals(lastName, admin.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), firstName, lastName);
    }

    @Override
    public String toString() {
        return String.format("Admin{id=%d, name='%s', designation='%s', username='%s'}",
                getId(), getFullName(), designation, getUsername());
    }
}
