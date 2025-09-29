package com.hospital.management.models;

import com.hospital.management.common.enums.UserRole;
import com.hospital.management.common.exceptions.ValidationException;
import com.hospital.management.common.utils.InputValidator;
import com.hospital.management.common.utils.DateTimeUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Patient model extending User with patient-specific information
 */
public class Patient extends User {

    public enum Gender {
        MALE("Male"),
        FEMALE("Female"),
        OTHER("Other");

        private final String displayName;

        Gender(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }

        @Override
        public String toString() { return displayName; }
    }

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String bloodGroup;
    private String address;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String insuranceNumber;
    private String medicalHistory;
    private String allergies;

    // Default constructor
    public Patient() {
        super();
        this.role = UserRole.PATIENT;
    }

    // Constructor with user fields
    public Patient(String username, String passwordHash, String email, String phone) {
        super(username, passwordHash, email, phone, UserRole.PATIENT);
    }

    // Constructor with patient-specific fields
    public Patient(String username, String passwordHash, String email, String phone,
                   String firstName, String lastName, LocalDate dateOfBirth, Gender gender) {
        super(username, passwordHash, email, phone, UserRole.PATIENT);
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
    }

    // Full constructor
    public Patient(Long id, String username, String passwordHash, String email, String phone,
                   boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt,
                   String firstName, String lastName, LocalDate dateOfBirth, Gender gender,
                   String bloodGroup, String address, String emergencyContactName,
                   String emergencyContactPhone, String insuranceNumber,
                   String medicalHistory, String allergies) {
        super(id, username, passwordHash, email, phone, UserRole.PATIENT, isActive, createdAt, updatedAt);
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.bloodGroup = bloodGroup;
        this.address = address;
        this.emergencyContactName = emergencyContactName;
        this.emergencyContactPhone = emergencyContactPhone;
        this.insuranceNumber = insuranceNumber;
        this.medicalHistory = medicalHistory;
        this.allergies = allergies;
    }

    @Override
    public void validate() throws ValidationException {
        super.validate(); // Validate base user fields

        InputValidator.validateName(firstName, "First Name");
        InputValidator.validateName(lastName, "Last Name");

        if (dateOfBirth == null) {
            throw new ValidationException("Date of birth is required", "DateOfBirth");
        }

        if (dateOfBirth.isAfter(LocalDate.now())) {
            throw new ValidationException("Date of birth cannot be in the future", "DateOfBirth", dateOfBirth);
        }

        if (gender == null) {
            throw new ValidationException("Gender is required", "Gender");
        }

        if (emergencyContactPhone != null && !emergencyContactPhone.trim().isEmpty()) {
            InputValidator.validatePhone(emergencyContactPhone, "Emergency Contact Phone");
        }

        if (emergencyContactName != null && !emergencyContactName.trim().isEmpty()) {
            InputValidator.validateName(emergencyContactName, "Emergency Contact Name");
        }
    }

    // Business methods
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public int getAge() {
        return dateOfBirth != null ? DateTimeUtil.calculateAge(dateOfBirth) : 0;
    }

    public boolean isMinor() {
        return getAge() < 18;
    }

    public boolean hasInsurance() {
        return insuranceNumber != null && !insuranceNumber.trim().isEmpty();
    }

    public boolean hasAllergies() {
        return allergies != null && !allergies.trim().isEmpty();
    }

    public boolean hasMedicalHistory() {
        return medicalHistory != null && !medicalHistory.trim().isEmpty();
    }

    @Override
    public String getDisplayName() {
        return getFullName() + " (Patient)";
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

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        updateTimestamp();
    }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) {
        this.gender = gender;
        updateTimestamp();
    }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
        updateTimestamp();
    }

    public String getAddress() { return address; }
    public void setAddress(String address) {
        this.address = address;
        updateTimestamp();
    }

    public String getEmergencyContactName() { return emergencyContactName; }
    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
        updateTimestamp();
    }

    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public void setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
        updateTimestamp();
    }

    public String getInsuranceNumber() { return insuranceNumber; }
    public void setInsuranceNumber(String insuranceNumber) {
        this.insuranceNumber = insuranceNumber;
        updateTimestamp();
    }

    public String getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
        updateTimestamp();
    }

    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) {
        this.allergies = allergies;
        updateTimestamp();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Patient)) return false;
        if (!super.equals(o)) return false;
        Patient patient = (Patient) o;
        return Objects.equals(firstName, patient.firstName) &&
                Objects.equals(lastName, patient.lastName) &&
                Objects.equals(dateOfBirth, patient.dateOfBirth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), firstName, lastName, dateOfBirth);
    }

    @Override
    public String toString() {
        return String.format("Patient{id=%d, name='%s', age=%d, gender=%s, phone='%s'}",
                getId(), getFullName(), getAge(), gender, getPhone());
    }
}
