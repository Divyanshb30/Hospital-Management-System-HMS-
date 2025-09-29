package com.hospital.management.models;

import com.hospital.management.common.enums.UserRole;
import com.hospital.management.common.exceptions.ValidationException;
import com.hospital.management.common.utils.InputValidator;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Doctor model extending User with doctor-specific information
 */
public class Doctor extends User {

    private String firstName;
    private String lastName;
    private String specialization;
    private String licenseNumber;
    private Long departmentId;
    private String qualification;
    private int experienceYears;
    private BigDecimal consultationFee;
    private LocalTime availableFrom;
    private LocalTime availableTo;
    private boolean isAvailable;

    // Default constructor
    public Doctor() {
        super();
        this.role = UserRole.DOCTOR;
        this.isAvailable = true;
        this.consultationFee = new BigDecimal("500.00");
        this.availableFrom = LocalTime.of(9, 0);
        this.availableTo = LocalTime.of(17, 0);
        this.experienceYears = 0;
    }

    // Constructor with user fields
    public Doctor(String username, String passwordHash, String email, String phone) {
        this();
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.phone = phone;
    }

    // Constructor with doctor-specific fields
    public Doctor(String username, String passwordHash, String email, String phone,
                  String firstName, String lastName, String specialization,
                  String licenseNumber, Long departmentId) {
        this(username, passwordHash, email, phone);
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
        this.licenseNumber = licenseNumber;
        this.departmentId = departmentId;
    }

    // Full constructor
    public Doctor(Long id, String username, String passwordHash, String email, String phone,
                  boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt,
                  String firstName, String lastName, String specialization, String licenseNumber,
                  Long departmentId, String qualification, int experienceYears,
                  BigDecimal consultationFee, LocalTime availableFrom, LocalTime availableTo,
                  boolean isDoctorAvailable) {
        super(id, username, passwordHash, email, phone, UserRole.DOCTOR, isActive, createdAt, updatedAt);
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
        this.licenseNumber = licenseNumber;
        this.departmentId = departmentId;
        this.qualification = qualification;
        this.experienceYears = experienceYears;
        this.consultationFee = consultationFee;
        this.availableFrom = availableFrom;
        this.availableTo = availableTo;
        this.isAvailable = isDoctorAvailable;
    }

    @Override
    public void validate() throws ValidationException {
        super.validate(); // Validate base user fields

        InputValidator.validateName(firstName, "First Name");
        InputValidator.validateName(lastName, "Last Name");
        InputValidator.validateRequired(specialization, "Specialization");
        InputValidator.validateRequired(licenseNumber, "License Number");

        if (departmentId == null) {
            throw new ValidationException("Department is required", "DepartmentId");
        }

        if (licenseNumber != null && (licenseNumber.length() < 5 || licenseNumber.length() > 50)) {
            throw new ValidationException("License number must be 5-50 characters", "LicenseNumber", licenseNumber);
        }

        if (experienceYears < 0 || experienceYears > 60) {
            throw new ValidationException("Experience years must be between 0 and 60", "ExperienceYears", experienceYears);
        }

        if (consultationFee != null && consultationFee.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Consultation fee cannot be negative", "ConsultationFee", consultationFee);
        }

        if (availableFrom != null && availableTo != null && availableFrom.isAfter(availableTo)) {
            throw new ValidationException("Available from time cannot be after available to time", "AvailableTime");
        }
    }

    // Business methods
    public String getFullName() {
        return "Dr. " + firstName + " " + lastName;
    }

    public String getDisplayName() {
        return getFullName() + " (" + specialization + ")";
    }

    public boolean isAvailableAtTime(LocalTime time) {
        if (!isAvailable) return false;
        if (availableFrom == null || availableTo == null) return true;
        return !time.isBefore(availableFrom) && time.isBefore(availableTo);
    }

    public String getAvailabilityWindow() {
        if (availableFrom == null || availableTo == null) {
            return "Not specified";
        }
        return availableFrom.toString() + " - " + availableTo.toString();
    }

    public boolean isExperienced() {
        return experienceYears >= 5;
    }

    public boolean isSpecialist() {
        return specialization != null && !specialization.equalsIgnoreCase("General Medicine");
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

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
        updateTimestamp();
    }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
        updateTimestamp();
    }

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
        updateTimestamp();
    }

    public String getQualification() { return qualification; }
    public void setQualification(String qualification) {
        this.qualification = qualification;
        updateTimestamp();
    }

    public int getExperienceYears() { return experienceYears; }
    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
        updateTimestamp();
    }

    public BigDecimal getConsultationFee() { return consultationFee; }
    public void setConsultationFee(BigDecimal consultationFee) {
        this.consultationFee = consultationFee;
        updateTimestamp();
    }

    public LocalTime getAvailableFrom() { return availableFrom; }
    public void setAvailableFrom(LocalTime availableFrom) {
        this.availableFrom = availableFrom;
        updateTimestamp();
    }

    public LocalTime getAvailableTo() { return availableTo; }
    public void setAvailableTo(LocalTime availableTo) {
        this.availableTo = availableTo;
        updateTimestamp();
    }

    public boolean isDoctorAvailable() { return isAvailable; }
    public void setDoctorAvailable(boolean available) {
        this.isAvailable = available;
        updateTimestamp();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Doctor)) return false;
        if (!super.equals(o)) return false;
        Doctor doctor = (Doctor) o;
        return Objects.equals(licenseNumber, doctor.licenseNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), licenseNumber);
    }

    @Override
    public String toString() {
        return String.format("Doctor{id=%d, name='%s', specialization='%s', department=%d, fee=₹%s}",
                getId(), getFullName(), specialization, departmentId, consultationFee);
    }
}
