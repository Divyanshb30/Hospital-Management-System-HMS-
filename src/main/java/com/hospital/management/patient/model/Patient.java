package com.hospital.management.patient.model;

import java.time.LocalDate;
import java.util.Objects;

//  Patient class
public class Patient {
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;       // Could be enum later (M, F, O)
    private String phone;
    private String email;
    private String address;
    private String bloodGroup;   // e.g. A+, O-
    private String status;       // Could map to enums.PatientStatus

    // --- Constructors ---
    public Patient() {}

    public Patient(Long id, String firstName, String lastName, LocalDate dateOfBirth,
                   String gender, String phone, String email, String address,
                   String bloodGroup, String status) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.bloodGroup = bloodGroup;
        this.status = status;
    }

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // --- Object methods ---

    /* This is used because lets say we call same patient 2 times from database they will be 2 java objects
        but because of this they will have same reference.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Patient)) return false;
        Patient patient = (Patient) o;
        return Objects.equals(id, patient.id);
    }

    /* When ever we override equals, we have to override hashCode because by default hashSet methods
        check for reference but we are setting hashing by id here.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", gender='" + gender + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", bloodGroup='" + bloodGroup + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}