package com.hospital.management.common.exceptions;

/**
 * Exception thrown when a doctor is not found
 */
public class DoctorNotFoundException extends HospitalManagementException {

    public DoctorNotFoundException(Long doctorId) {
        super(String.format("Doctor not found with ID: %d", doctorId), "DOCTOR_NOT_FOUND");
    }

    public DoctorNotFoundException(String identifier) {
        super(String.format("Doctor not found with identifier: %s", identifier), "DOCTOR_NOT_FOUND");
    }
}
