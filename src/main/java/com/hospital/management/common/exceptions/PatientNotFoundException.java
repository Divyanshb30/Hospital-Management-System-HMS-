package com.hospital.management.common.exceptions;

/**
 * Exception thrown when a patient is not found
 */
public class PatientNotFoundException extends HospitalManagementException {

    public PatientNotFoundException(Long patientId) {
        super(String.format("Patient not found with ID: %d", patientId), "PATIENT_NOT_FOUND");
    }

    public PatientNotFoundException(String identifier) {
        super(String.format("Patient not found with identifier: %s", identifier), "PATIENT_NOT_FOUND");
    }
}
