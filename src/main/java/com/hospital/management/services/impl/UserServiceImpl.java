package com.hospital.management.services.impl;

import com.hospital.management.interfaces.UserService;
import com.hospital.management.dao.interfaces.UserDAO;
import com.hospital.management.dao.interfaces.PatientDAO;
import com.hospital.management.dao.impl.UserDAOImpl;
import com.hospital.management.dao.impl.PatientDAOImpl;
import com.hospital.management.models.User;
import com.hospital.management.models.Patient;
import com.hospital.management.common.utils.PasswordEncoder;
import com.hospital.management.common.exceptions.ValidationException;

import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {

    private final UserDAO userDAO = new UserDAOImpl();
    private final PatientDAO patientDAO = new PatientDAOImpl();

    @Override
    public Optional<User> findUserById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        User user = userDAO.getUserById(id.intValue());
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return Optional.empty();
        }
        List<User> users = userDAO.getAllUsers();
        return users.stream()
                .filter(u -> username.equals(u.getUsername()))
                .findFirst();
    }

    @Override
    public List<User> findAllUsers() {
        return userDAO.getAllUsers();
    }

    @Override
    public boolean registerUser(User user, String password) {
        if (user == null || password == null || password.isEmpty()) {
            return false;
        }

        try {
            System.out.println("🔄 Starting user registration process...");

            // Encode password first (before validation)
            String encodedPassword = PasswordEncoder.encodePassword(password);
            user.setPasswordHash(encodedPassword);

            // Validate user fields
            user.validate();

            // STEP 1: Save to users table first
            System.out.println("🔄 Saving user data to users table...");
            boolean userCreated = userDAO.createUser(user);

            if (!userCreated) {
                System.err.println("❌ Failed to create user in users table");
                return false;
            }

            System.out.println("✅ User saved to users table successfully");

            // STEP 2: Get the generated user ID from database
            Optional<User> savedUser = findUserByUsername(user.getUsername());
            if (savedUser.isEmpty()) {
                System.err.println("❌ Could not retrieve saved user from database");
                return false;
            }

            Long userId = savedUser.get().getId();
            user.setId(userId); // Set the generated ID in the user object

            System.out.println("✅ Retrieved generated user ID: " + userId);

            // STEP 3: If it's a Patient, save patient-specific data
            if (user instanceof Patient) {
                System.out.println("🔄 User is a Patient, saving patient-specific data...");

                Patient patient = (Patient) user;
                patient.setId(userId); // Set user_id for foreign key relationship

                System.out.println("🔄 Saving patient data to patients table...");
                boolean patientCreated = patientDAO.createPatient(patient);

                if (!patientCreated) {
                    System.err.println("❌ Failed to save patient data to patients table");
                    // Optionally: rollback user creation here if needed
                    return false;
                }

                System.out.println("✅ Patient data saved to patients table successfully");
                System.out.println("🎉 Complete patient registration successful!");
            } else {
                System.out.println("✅ Non-patient user registration completed");
            }

            return true;

        } catch (ValidationException e) {
            System.err.println("❌ User validation failed during registration: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("❌ Unexpected error during registration: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateUser(User user) {
        if (user == null || user.getId() == null) {
            return false;
        }

        try {
            user.validate();

            // Update user in users table
            boolean userUpdated = userDAO.updateUser(user);

            // If it's a patient, also update patient-specific data
            if (userUpdated && user instanceof Patient) {
                Patient patient = (Patient) user;
                // You might want to add patientDAO.updatePatient(patient) here
                // if you have that method implemented
            }

            return userUpdated;

        } catch (ValidationException e) {
            System.err.println("User validation failed during update: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteUser(Long id) {
        if (id == null) {
            return false;
        }

        try {
            // Note: In a real application, you might want to handle cascade deletion
            // or check if patient data exists before deleting user
            return userDAO.deleteUser(id.intValue());
        } catch (Exception e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean authenticate(String username, String password) {
        if (username == null || password == null) {
            return false;
        }

        try {
            Optional<User> userOpt = findUserByUsername(username);

            if (userOpt.isEmpty()) {
                System.out.println("❌ User not found: " + username);
                return false;
            }

            User user = userOpt.get();

            if (!user.isActive()) {
                System.out.println("❌ User account is inactive: " + username);
                return false;
            }

            boolean passwordValid = PasswordEncoder.verifyPassword(password, user.getPasswordHash());

            if (passwordValid) {
                System.out.println("✅ Authentication successful for user: " + username);
            } else {
                System.out.println("❌ Invalid password for user: " + username);
            }

            return passwordValid;

        } catch (Exception e) {
            System.err.println("Authentication error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Helper method to find patient by user ID
     * You can use this in other parts of your application
     */
    public Optional<Patient> findPatientByUserId(Long userId) {
        if (userId == null) {
            return Optional.empty();
        }

        try {
            Patient patient = patientDAO.getPatientById(userId.intValue());
            return Optional.ofNullable(patient);
        } catch (Exception e) {
            System.err.println("Error finding patient: " + e.getMessage());
            return Optional.empty();
        }
    }
}
