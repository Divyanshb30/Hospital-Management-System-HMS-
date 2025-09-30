package com.hospital.management.commands.AdminCommands;

import com.hospital.management.commands.Command;
import com.hospital.management.commands.CommandResult;
import com.hospital.management.common.exceptions.DatabaseException;
import com.hospital.management.common.exceptions.ValidationException;
import com.hospital.management.dao.interfaces.DoctorDAO;
import com.hospital.management.dao.impl.DoctorDAOImpl;
import com.hospital.management.interfaces.UserService;
import com.hospital.management.models.Doctor;
import com.hospital.management.models.User;
import com.hospital.management.common.enums.UserRole;

import java.math.BigDecimal;
import java.time.LocalTime;

public class AddDoctorCommand implements Command {

    private final Long adminId;
    private final String username;
    private final String password;
    private final String email;
    private final String phone;
    private final String firstName;
    private final String lastName;
    private final String specialization;
    private final String licenseNumber;
    private final Long departmentId;
    private final String qualification;
    private final int experienceYears;
    private final BigDecimal consultationFee;
    private final UserService userService;
    private final DoctorDAO doctorDAO;

    public AddDoctorCommand(Long adminId, String username, String password, String email, String phone,
                            String firstName, String lastName, String specialization, String licenseNumber,
                            Long departmentId, String qualification, int experienceYears,
                            BigDecimal consultationFee, UserService userService) {
        this.adminId = adminId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
        this.licenseNumber = licenseNumber;
        this.departmentId = departmentId;
        this.qualification = qualification;
        this.experienceYears = experienceYears;
        this.consultationFee = consultationFee;
        this.userService = userService;
        this.doctorDAO = new DoctorDAOImpl();
    }

    @Override
    public CommandResult execute() throws ValidationException, DatabaseException {
        if (!validateParameters()) {
            throw new ValidationException("Invalid parameters provided", "AddDoctor");
        }

        try {
            // Check if username or email already exists
            if (userService.findUserByUsername(username).isPresent()) {
                return CommandResult.failure("Username already exists: " + username, null);
            }

            if (userService.findUserByEmail(email).isPresent()) {
                return CommandResult.failure("Email already exists: " + email, null);
            }

            // ✅ Create Doctor object with ALL fields set first
            Doctor doctor = new Doctor(username, password, email, phone,
                    firstName, lastName, specialization,
                    licenseNumber, departmentId);

            // Set additional fields
            doctor.setQualification(qualification);
            doctor.setExperienceYears(experienceYears);
            doctor.setConsultationFee(consultationFee);
            doctor.setAvailableFrom(LocalTime.of(9, 0));
            doctor.setAvailableTo(LocalTime.of(17, 0));
            doctor.setDoctorAvailable(true);

            // ✅ Validate doctor data BEFORE registering
            doctor.validate();

            // ✅ Register the doctor (this will call validate again but now all fields are set)
            boolean userRegistered = userService.registerUser(doctor, password);
            if (!userRegistered) {
                return CommandResult.failure("Failed to create user account for doctor", null);
            }

            return CommandResult.success("Doctor added successfully", doctor);

        } catch (ValidationException ve) {
            throw ve;
        } catch (Exception e) {
            throw new DatabaseException("Error adding doctor: " + e.getMessage(), "DOCTOR_CREATION_ERROR");
        }
    }

    @Override
    public String getDescription() {
        return "Add new doctor to the system";
    }

    @Override
    public boolean validateParameters() throws ValidationException {
        if (adminId == null || adminId <= 0) {
            throw new ValidationException("Valid admin ID is required", "AdminId");
        }

        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("Username is required", "Username");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new ValidationException("Password is required", "Password");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email is required", "Email");
        }

        if (phone == null || phone.trim().isEmpty()) {
            throw new ValidationException("Phone is required", "Phone");
        }

        if (firstName == null || firstName.trim().isEmpty()) {
            throw new ValidationException("First name is required", "FirstName");
        }

        if (lastName == null || lastName.trim().isEmpty()) {
            throw new ValidationException("Last name is required", "LastName");
        }

        if (specialization == null || specialization.trim().isEmpty()) {
            throw new ValidationException("Specialization is required", "Specialization");
        }

        if (licenseNumber == null || licenseNumber.trim().isEmpty()) {
            throw new ValidationException("License number is required", "LicenseNumber");
        }

        if (departmentId == null || departmentId <= 0) {
            throw new ValidationException("Valid department ID is required", "DepartmentId");
        }

        return true;
    }
}
