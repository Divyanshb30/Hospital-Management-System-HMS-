package com.hospital.management.patient.model;

import java.util.Objects;

/**
 * EmergencyContact represents a person to be contacted
 * in case of a patient's emergency.
 */
public class EmergencyContact {
    private Long id;          // Unique identifier (DB PK)
    private Long patientId;   // Link to Patient.id
    private String name;      // Contact person's name
    private String relation;  // Relation to patient (Father, Mother, Spouse, etc.)
    private String phone;     // Contact number
    private String email;     // Optional email
    private String address;   // Optional address

    // --- Constructors ---
    public EmergencyContact() {}

    public EmergencyContact(Long id, Long patientId, String name,
                            String relation, String phone,
                            String email, String address) {
        this.id = id;
        this.patientId = patientId;
        this.name = name;
        this.relation = relation;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    // Convenience constructor (before persisting, no id yet)
    public EmergencyContact(Long patientId, String name, String relation, String phone) {
        this(null, patientId, name, relation, phone, null, null);
    }

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRelation() { return relation; }
    public void setRelation(String relation) { this.relation = relation; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    // --- Equality & Hashing ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmergencyContact)) return false;
        EmergencyContact that = (EmergencyContact) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return (id != null) ? id.hashCode() : super.hashCode();
    }

    @Override
    public String toString() {
        return "EmergencyContact{" +
                "id=" + id +
                ", patientId=" + patientId +
                ", name='" + name + '\'' +
                ", relation='" + relation + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}