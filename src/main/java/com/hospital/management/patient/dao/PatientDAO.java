package com.hospital.management.patient.dao;

import com.hospital.management.patient.model.Patient;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object (DAO) interface for Patient entity.
 * Defines CRUD operations to be implemented using JDBC.
 */
public interface PatientDAO {

    /** Insert a new patient and return the generated ID. */
    Long insert(Patient patient);

    /** Retrieve a patient by primary key. */
    Optional<Patient> findById(Long id);

    /** Retrieve all patients (can later be paginated). */
    List<Patient> findAll();

    /** Search patients by name, email, or phone. */
    List<Patient> search(String query);

    /** Update an existing patient. Returns true if updated. */
    boolean update(Patient patient);

    /** Delete a patient by ID. Returns true if deleted. */
    boolean deleteById(Long id);

    /** Count all patients. */
    long count();
}
