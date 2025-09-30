package com.hospital.management.commands.AdminCommands;

import com.hospital.management.commands.Command;
import com.hospital.management.commands.CommandResult;
import com.hospital.management.common.exceptions.DatabaseException;
import com.hospital.management.common.exceptions.ValidationException;
import com.hospital.management.interfaces.UserService;
import com.hospital.management.models.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ViewAdminProfileCommand implements Command {

    private final Long adminId;
    private final UserService userService;

    public ViewAdminProfileCommand(Long adminId, UserService userService) {
        this.adminId = adminId;
        this.userService = userService;
    }

    @Override
    public CommandResult execute() throws ValidationException, DatabaseException {
        if (!validateParameters()) {
            throw new ValidationException("Invalid admin ID", "AdminId");
        }

        try {
            Optional<User> userOpt = userService.findUserById(adminId);
            if (userOpt.isEmpty()) {
                return CommandResult.failure("Admin not found", null);
            }

            User admin = userOpt.get();

            // Create admin profile data
            Map<String, Object> profileData = new HashMap<>();
            profileData.put("id", admin.getId());
            profileData.put("username", admin.getUsername());
            profileData.put("email", admin.getEmail());
            profileData.put("phone", admin.getPhone());
            profileData.put("role", admin.getRole());
            profileData.put("isActive", admin.isActive());
            profileData.put("createdAt", admin.getCreatedAt());
            profileData.put("updatedAt", admin.getUpdatedAt());

            return CommandResult.success("Admin profile retrieved successfully", profileData);

        } catch (Exception e) {
            throw new DatabaseException("Error retrieving admin profile: " + e.getMessage(), "ADMIN_PROFILE_ERROR");
        }
    }

    @Override
    public String getDescription() {
        return "View admin profile details";
    }

    @Override
    public boolean validateParameters() throws ValidationException {
        return adminId != null && adminId > 0;
    }
}
