package com.hospital.management.commands.DoctorCommands;

import com.hospital.management.commands.Command;
import com.hospital.management.commands.CommandResult;
import com.hospital.management.common.exceptions.DatabaseException;
import com.hospital.management.common.exceptions.ValidationException;
import com.hospital.management.common.exceptions.BusinessLogicException;
import com.hospital.management.interfaces.DoctorService;  // ✅ Use DoctorService
import com.hospital.management.models.Doctor;
import java.util.Optional;

public class UpdateProfileCommand implements Command {
    private final Long doctorId;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String phone;
    private final String specialization;

    // ✅ Use DoctorService instead of UserService
    private final DoctorService doctorService;

    public UpdateProfileCommand(Long doctorId, String firstName, String lastName, String email,
                                String phone, String specialization, DoctorService doctorService) {
        this.doctorId = doctorId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.specialization = specialization;
        this.doctorService = doctorService;  // ✅ Use DoctorService
    }

    @Override
    public CommandResult execute() throws DatabaseException, ValidationException, BusinessLogicException {
        try {
            // Validate parameters first
            if (!validateParameters()) {
                return CommandResult.failure("Invalid parameters for profile update");
            }

            // ✅ Get existing doctor from DoctorService using doctor_id
            Optional<Doctor> doctorOptional = doctorService.findDoctorById(doctorId);

            if (!doctorOptional.isPresent()) {
                throw new BusinessLogicException("Doctor not found with ID: " + doctorId);
            }

            Doctor existingDoctor = doctorOptional.get();

            // Update doctor fields
            updateDoctorFields(existingDoctor);

            // ✅ Use DoctorService to update profile
            boolean success = doctorService.updateDoctor(existingDoctor);

            if (success) {
                return CommandResult.success("Doctor profile updated successfully", existingDoctor);
            } else {
                throw new BusinessLogicException("Failed to update doctor profile");
            }

        } catch (ValidationException | BusinessLogicException e) {
            return CommandResult.failure("Profile update failed: " + e.getMessage(), e);
        } catch (Exception e) {
            return CommandResult.failure("Unexpected error during profile update: " + e.getMessage(), e);
        }
    }

    @Override
    public String getDescription() {
        return "Update profile for doctor ID: " + doctorId;
    }

    @Override
    public boolean validateParameters() throws ValidationException {
        if (doctorService == null) {
            throw new ValidationException("DoctorService is required", "DoctorService");
        }

        if (doctorId == null || doctorId <= 0) {
            throw new ValidationException("Valid doctor ID is required", "DoctorId", doctorId);
        }

        // At least one field must be provided for update
        if ((firstName == null || firstName.trim().isEmpty()) &&
                (lastName == null || lastName.trim().isEmpty()) &&
                (email == null || email.trim().isEmpty()) &&
                (phone == null || phone.trim().isEmpty()) &&
                (specialization == null || specialization.trim().isEmpty())) {
            throw new ValidationException("At least one field must be provided for update", "UpdateFields");
        }

        return true;
    }

    private void updateDoctorFields(Doctor doctor) {
        // Update only fields that are provided (not null/empty)
        if (firstName != null && !firstName.trim().isEmpty()) {
            doctor.setFirstName(firstName.trim());
        }
        if (lastName != null && !lastName.trim().isEmpty()) {
            doctor.setLastName(lastName.trim());
        }
        if (email != null && !email.trim().isEmpty()) {
            doctor.setEmail(email.trim().toLowerCase());
        }
        if (phone != null && !phone.trim().isEmpty()) {
            doctor.setPhone(phone.trim());
        }
        if (specialization != null && !specialization.trim().isEmpty()) {
            doctor.setSpecialization(specialization.trim());
        }
    }
}
