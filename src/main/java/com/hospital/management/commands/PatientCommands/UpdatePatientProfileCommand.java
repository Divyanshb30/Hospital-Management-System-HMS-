package com.hospital.management.commands.PatientCommands;

import com.hospital.management.commands.Command;
import com.hospital.management.commands.CommandResult;
import com.hospital.management.common.exceptions.DatabaseException;
import com.hospital.management.common.exceptions.ValidationException;
import com.hospital.management.common.exceptions.BusinessLogicException;
import com.hospital.management.common.utils.InputValidator;
import com.hospital.management.interfaces.UserService;
import com.hospital.management.models.Patient;
import com.hospital.management.models.User;
import com.hospital.management.dao.interfaces.PatientDAO;
import com.hospital.management.dao.impl.PatientDAOImpl;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Command to update patient profile information
 * Uses UserService and PatientDAO to update both user and patient data
 */
public class UpdatePatientProfileCommand implements Command {

    private final Long patientId;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String phone;
    private final LocalDate dateOfBirth;
    private final Patient.Gender gender;
    private final String bloodGroup;
    private final String address;
    private final String emergencyContactName;
    private final String emergencyContactPhone;

    // Service dependencies
    private final UserService userService;
    private final PatientDAO patientDAO;

    // Constructor for full profile update
    public UpdatePatientProfileCommand(Long patientId, String firstName, String lastName,
                                       String email, String phone, LocalDate dateOfBirth,
                                       Patient.Gender gender, String bloodGroup, String address,
                                       String emergencyContactName, String emergencyContactPhone,
                                       UserService userService) {
        this.patientId = patientId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.bloodGroup = bloodGroup;
        this.address = address;
        this.emergencyContactName = emergencyContactName;
        this.emergencyContactPhone = emergencyContactPhone;
        this.userService = userService;
        this.patientDAO = new PatientDAOImpl();
    }

    // Constructor for basic profile update (just user fields)
    public UpdatePatientProfileCommand(Long patientId, String firstName, String lastName,
                                       String email, String phone, UserService userService) {
        this.patientId = patientId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.dateOfBirth = null;
        this.gender = null;
        this.bloodGroup = null;
        this.address = null;
        this.emergencyContactName = null;
        this.emergencyContactPhone = null;
        this.userService = userService;
        this.patientDAO = new PatientDAOImpl();
    }

    @Override
    public CommandResult execute() throws ValidationException, BusinessLogicException, DatabaseException {
        try {
            // Validate parameters
            if (!validateParameters()) {
                throw new ValidationException("Invalid parameters provided", "UpdatePatientProfile");
            }

            // Get current user/patient data
            Optional<User> userOpt = userService.findUserById(patientId);
            if (userOpt.isEmpty()) {
                throw new BusinessLogicException("Patient not found with ID: " + patientId, "PATIENT_NOT_FOUND");
            }

            User user = userOpt.get();

            // Update user fields if provided
            if (email != null && !email.trim().isEmpty()) {
                user.setEmail(email.trim());
            }
            if (phone != null && !phone.trim().isEmpty()) {
                user.setPhone(phone.trim());
            }

            // Update user in database
            boolean userUpdated = userService.updateUser(user);
            if (!userUpdated) {
                throw new DatabaseException("Failed to update user information", "USER_UPDATE_FAILED");
            }

            // Update patient-specific fields if any are provided
            if (hasPatientSpecificUpdates()) {
                Patient patient = patientDAO.getPatientById(patientId.intValue());
                if (patient != null) {
                    updatePatientFields(patient);

                    boolean patientUpdated = patientDAO.updatePatient(patient);
                    if (!patientUpdated) {
                        throw new DatabaseException("Failed to update patient information", "PATIENT_UPDATE_FAILED");
                    }
                }
            }

            return CommandResult.success("Patient profile updated successfully", user);

        } catch (ValidationException | BusinessLogicException | DatabaseException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Unexpected error during profile update: " + e.getMessage(), "UNEXPECTED_ERROR");
        }
    }

    // ✅ FIX 1: Add the missing getDescription() method
    @Override
    public String getDescription() {
        return "Update patient profile for patient ID: " + patientId;
    }

    // ✅ FIX 2: Make validateParameters() public (was private)
    @Override
    public boolean validateParameters() throws ValidationException {
        if (patientId == null || patientId <= 0) {
            throw new ValidationException("Valid patient ID is required", "PatientId");
        }

        if (userService == null) {
            throw new ValidationException("UserService is required", "UserService");
        }

        // Validate email format if provided
        if (email != null && !email.trim().isEmpty() && !InputValidator.isValidEmail(email)) {
            throw new ValidationException("Invalid email format", "Email");
        }

        // Validate phone format if provided
        if (phone != null && !phone.trim().isEmpty() && !InputValidator.isValidPhone(phone)) {
            throw new ValidationException("Invalid phone format", "Phone");
        }

        // Validate names if provided
        if (firstName != null && !firstName.trim().isEmpty() && !InputValidator.isValidName(firstName)) {
            throw new ValidationException("Invalid first name", "FirstName");
        }

        if (lastName != null && !lastName.trim().isEmpty() && !InputValidator.isValidName(lastName)) {
            throw new ValidationException("Invalid last name", "LastName");
        }

        return true;
    }

    private boolean hasPatientSpecificUpdates() {
        return dateOfBirth != null || gender != null ||
                (bloodGroup != null && !bloodGroup.trim().isEmpty()) ||
                (address != null && !address.trim().isEmpty()) ||
                (emergencyContactName != null && !emergencyContactName.trim().isEmpty()) ||
                (emergencyContactPhone != null && !emergencyContactPhone.trim().isEmpty());
    }

    private void updatePatientFields(Patient patient) {
        if (firstName != null && !firstName.trim().isEmpty()) {
            patient.setFirstName(firstName.trim());
        }
        if (lastName != null && !lastName.trim().isEmpty()) {
            patient.setLastName(lastName.trim());
        }
        if (dateOfBirth != null) {
            patient.setDateOfBirth(dateOfBirth);
        }
        if (gender != null) {
            patient.setGender(gender);
        }
        if (bloodGroup != null && !bloodGroup.trim().isEmpty()) {
            patient.setBloodGroup(bloodGroup.trim().toUpperCase());
        }
        if (address != null && !address.trim().isEmpty()) {
            patient.setAddress(address.trim());
        }
        if (emergencyContactName != null && !emergencyContactName.trim().isEmpty()) {
            patient.setEmergencyContactName(emergencyContactName.trim());
        }
        if (emergencyContactPhone != null && !emergencyContactPhone.trim().isEmpty()) {
            patient.setEmergencyContactPhone(emergencyContactPhone.trim());
        }
    }
}
