package com.hospital.management.commands.PatientCommands;

import com.hospital.management.commands.Command;
import com.hospital.management.commands.CommandResult;
import com.hospital.management.common.exceptions.DatabaseException;
import com.hospital.management.common.exceptions.ValidationException;
import com.hospital.management.common.exceptions.BusinessLogicException;
import com.hospital.management.interfaces.UserService;
import com.hospital.management.models.Patient;
import com.hospital.management.models.User;
import com.hospital.management.dao.interfaces.PatientDAO;
import com.hospital.management.dao.impl.PatientDAOImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Command to view complete patient profile information
 * Uses UserService and PatientDAO to retrieve both user and patient data
 */
public class ViewPatientProfileCommand implements Command {

    private final Long patientId;
    private final UserService userService;
    private final PatientDAO patientDAO;

    public ViewPatientProfileCommand(Long patientId, UserService userService) {
        this.patientId = patientId;
        this.userService = userService;
        this.patientDAO = new PatientDAOImpl();
    }

    @Override
    public CommandResult execute() throws ValidationException, BusinessLogicException, DatabaseException {
        // Validate parameters
        if (!validateParameters()) {
            throw new ValidationException("Invalid parameters provided", "ViewPatientProfile");
        }

        try {
            // Get user data
            Optional<User> userOpt = userService.findUserById(patientId);
            if (userOpt.isEmpty()) {
                throw new BusinessLogicException("Patient not found with ID: " + patientId, "PATIENT_NOT_FOUND");
            }

            User user = userOpt.get();

            // ✅ FIX: Get patient-specific data by USER_ID, not patient ID
            Patient patient = patientDAO.getPatientByUserId(patientId.longValue());

            // Create comprehensive profile data
            Map<String, Object> profileData = new HashMap<>();

            // User data
            profileData.put("id", user.getId());
            profileData.put("username", user.getUsername());
            profileData.put("email", user.getEmail());
            profileData.put("phone", user.getPhone());
            profileData.put("role", user.getRole());
            profileData.put("isActive", user.isActive());
            profileData.put("createdAt", user.getCreatedAt());
            profileData.put("updatedAt", user.getUpdatedAt());

            // Patient data
            if (patient != null) {
                profileData.put("firstName", patient.getFirstName());
                profileData.put("lastName", patient.getLastName());
                profileData.put("dateOfBirth", patient.getDateOfBirth());
                profileData.put("age", patient.getAge());
                profileData.put("gender", patient.getGender());
                profileData.put("bloodGroup", patient.getBloodGroup());
                profileData.put("address", patient.getAddress());
                profileData.put("emergencyContactName", patient.getEmergencyContactName());
                profileData.put("emergencyContactPhone", patient.getEmergencyContactPhone());
                profileData.put("insuranceNumber", patient.getInsuranceNumber());
                profileData.put("medicalHistory", patient.getMedicalHistory());
                profileData.put("allergies", patient.getAllergies());
                profileData.put("displayName", patient.getDisplayName());
            } else {
                // Set null values if patient data not found
                profileData.put("firstName", null);
                profileData.put("lastName", null);
                profileData.put("dateOfBirth", null);
                profileData.put("age", null);
                profileData.put("gender", null);
                profileData.put("bloodGroup", null);
                profileData.put("address", null);
                profileData.put("emergencyContactName", null);
                profileData.put("emergencyContactPhone", null);
                profileData.put("insuranceNumber", null);
                profileData.put("medicalHistory", null);
                profileData.put("allergies", null);
                profileData.put("displayName", user.getUsername());
            }

            return CommandResult.success("Patient profile retrieved successfully", profileData);

        } catch (Exception e) {
            // Convert any unexpected exception to DatabaseException
            throw new DatabaseException("Unexpected error during profile retrieval: " + e.getMessage(), "UNEXPECTED_ERROR");
        }
    }

    @Override
    public String getDescription() {
        return "View complete patient profile for patient ID: " + patientId;
    }

    @Override
    public boolean validateParameters() throws ValidationException {
        if (patientId == null || patientId <= 0) {
            throw new ValidationException("Valid patient ID is required", "PatientId");
        }

        if (userService == null) {
            throw new ValidationException("UserService is required", "UserService");
        }

        return true;
    }
}
