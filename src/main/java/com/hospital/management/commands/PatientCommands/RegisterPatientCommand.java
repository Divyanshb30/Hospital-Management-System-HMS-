package com.hospital.management.commands.PatientCommands;

import com.hospital.management.commands.Command;
import com.hospital.management.commands.CommandResult;
import com.hospital.management.common.exceptions.DatabaseException;
import com.hospital.management.common.exceptions.ValidationException;
import com.hospital.management.common.exceptions.BusinessLogicException;
import com.hospital.management.common.utils.InputValidator;
import com.hospital.management.interfaces.UserService;
import com.hospital.management.models.Patient;
import java.time.LocalDate;

/**
 * Command to register a new patient in the system
 * Uses UserService for registration
 */
public class RegisterPatientCommand implements Command {

    private final String username;
    private final String password;
    private final String email;
    private final String phone;
    private final String firstName;
    private final String lastName;
    private final LocalDate dateOfBirth;
    private final Patient.Gender gender;
    private final String bloodGroup;
    private final String address;
    private final String emergencyContactName;
    private final String emergencyContactPhone;

    // Service dependency
    private final UserService userService;

    // Constructor with required fields
    public RegisterPatientCommand(String username, String password, String email, String phone,
                                  String firstName, String lastName, LocalDate dateOfBirth,
                                  Patient.Gender gender, UserService userService) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.bloodGroup = null;
        this.address = null;
        this.emergencyContactName = null;
        this.emergencyContactPhone = null;
        this.userService = userService;
    }

    // Constructor with all fields
    public RegisterPatientCommand(String username, String password, String email, String phone,
                                  String firstName, String lastName, LocalDate dateOfBirth,
                                  Patient.Gender gender, String bloodGroup, String address,
                                  String emergencyContactName, String emergencyContactPhone,
                                  UserService userService) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.bloodGroup = bloodGroup;
        this.address = address;
        this.emergencyContactName = emergencyContactName;
        this.emergencyContactPhone = emergencyContactPhone;
        this.userService = userService;
    }

    @Override
    public CommandResult execute() throws DatabaseException, ValidationException, BusinessLogicException {
        try {
            // Validate parameters first
            if (!validateParameters()) {
                return CommandResult.failure("Invalid parameters for patient registration");
            }

            // Create patient object
            Patient patient = createPatient();

            // Use service to register patient
            boolean success = userService.registerUser(patient, password);

            if (success) {
                return CommandResult.success("Patient registered successfully", patient);
            } else {
                throw new BusinessLogicException("Failed to register patient");
            }

        } catch (ValidationException | BusinessLogicException e) {
            return CommandResult.failure("Registration failed: " + e.getMessage(), e);
        } catch (Exception e) {
            return CommandResult.failure("Unexpected error during registration: " + e.getMessage(), e);
        }
    }

    @Override
    public String getDescription() {
        return "Register new patient: " + firstName + " " + lastName + " (" + username + ")";
    }

    @Override
    public boolean validateParameters() throws ValidationException {
        if (userService == null) {
            throw new ValidationException("UserService is required", "UserService");
        }

        InputValidator.validateRequired(username, "Username");
        InputValidator.validateRequired(password, "Password");
        InputValidator.validateEmail(email, "Email");
        InputValidator.validatePhone(phone, "Phone");
        InputValidator.validateName(firstName, "First Name");
        InputValidator.validateName(lastName, "Last Name");

        if (dateOfBirth == null) {
            throw new ValidationException("Date of birth is required", "DateOfBirth");
        }

        if (gender == null) {
            throw new ValidationException("Gender is required", "Gender");
        }

        return true;
    }

    private Patient createPatient() {
        Patient patient = new Patient();

        // Set basic fields
        patient.setUsername(username);
        patient.setEmail(email);
        patient.setPhone(phone);
        patient.setFirstName(firstName);
        patient.setLastName(lastName);
        patient.setDateOfBirth(dateOfBirth);
        patient.setGender(gender);

        // ✅ FIX: Set a temporary password hash so validation passes
        patient.setPasswordHash("TEMP"); // Will be replaced in UserService

        // Set optional fields
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

        return patient;
    }

}
