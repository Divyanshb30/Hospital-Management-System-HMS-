package com.hospital.management.models;

import com.hospital.management.common.enums.UserRole;
import com.hospital.management.common.exceptions.ValidationException;
import com.hospital.management.common.utils.InputValidator;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Abstract base class for all user types in the hospital management system
 * Implements common user properties and validation
 */
public abstract class User {

    protected Long id;
    protected String username;
    protected String passwordHash;
    protected String email;
    protected String phone;
    protected UserRole role;
    protected boolean isActive;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;

    // Default constructor
    public User() {
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor with essential fields
    public User(String username, String passwordHash, String email, String phone, UserRole role) {
        this();
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    // Full constructor
    public User(Long id, String username, String passwordHash, String email, String phone,
                UserRole role, boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Validates the user object
     * @throws ValidationException if validation fails
     */
    public void validate() throws ValidationException {
        InputValidator.validateRequired(username, "Username");
        InputValidator.validateRequired(passwordHash, "Password");
        InputValidator.validateEmail(email, "Email");
        InputValidator.validatePhone(phone, "Phone");

        if (role == null) {
            throw new ValidationException("User role is required", "Role");
        }

        if (!InputValidator.isValidUsername(username)) {
            throw new ValidationException("Username must be 3-20 characters, alphanumeric and underscore only", "Username", username);
        }
    }

    /**
     * Updates the updatedAt timestamp
     */
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) {
        this.username = username;
        updateTimestamp();
    }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        updateTimestamp();
    }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        this.email = email;
        updateTimestamp();
    }

    public String getPhone() { return phone; }
    public void setPhone(String phone) {
        this.phone = phone;
        updateTimestamp();
    }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) {
        this.role = role;
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
        return username + " (" + role.getDisplayName() + ")";
    }

    public boolean isPatient() { return role == UserRole.PATIENT; }
    public boolean isDoctor() { return role == UserRole.DOCTOR; }
    public boolean isAdmin() { return role == UserRole.ADMIN; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }

    @Override
    public String toString() {
        return String.format("User{id=%d, username='%s', email='%s', role=%s, active=%s}",
                id, username, email, role, isActive);
    }
}
