package com.hospital.management.patient.service;

import com.hospital.management.patient.model.Patient;

import java.util.List;
import java.util.Optional;

/**
 * Service layer interface for Patient operations.
 * Encapsulates business logic and acts as a bridge
 * between controllers (CLI/API) and DAO layer.
 */
public interface PatientService {

    /** Register a new patient. Returns generated ID. */
    Long registerPatient(Patient patient);

    /** Fetch patient by ID. */
    Optional<Patient> getPatientById(Long id);

    /** Fetch all patients (could be paginated in future). */
    List<Patient> getAllPatients();

    /** Search by name/email/phone. */
    List<Patient> searchPatients(String query);

    /** Update an existing patient. */
    boolean updatePatient(Patient patient);

    /** Discharge a patient (set status = DISCHARGED). */
    boolean dischargePatient(Long id);

    /** Delete patient record by ID. */
    boolean deletePatient(Long id);

    /** Count all registered patients. */
    long getTotalPatients();
}
