package com.hospital.management.commands.AdminCommands;

import com.hospital.management.commands.Command;
import com.hospital.management.commands.CommandResult;
import com.hospital.management.common.exceptions.DatabaseException;
import com.hospital.management.common.exceptions.ValidationException;
import com.hospital.management.common.exceptions.BusinessLogicException;
import com.hospital.management.interfaces.UserService;
import com.hospital.management.models.User;
import java.util.List;
import java.util.Optional;

/**
 * Command to manage users (view, delete users)
 * Uses UserService for user management operations
 */
public class ManageUsersCommand implements Command {

    public enum UserManagementAction {
        VIEW_ALL, GET_USER_DETAILS, DELETE_USER
    }

    private final Long adminId;
    private final UserManagementAction action;
    private final Long targetUserId;

    // Service dependency
    private final UserService userService;

    // Constructor for viewing all users
    public ManageUsersCommand(Long adminId, UserService userService) {
        this.adminId = adminId;
        this.action = UserManagementAction.VIEW_ALL;
        this.targetUserId = null;
        this.userService = userService;
    }

    // Constructor for user-specific actions
    public ManageUsersCommand(Long adminId, UserManagementAction action, Long targetUserId,
                              UserService userService) {
        this.adminId = adminId;
        this.action = action;
        this.targetUserId = targetUserId;
        this.userService = userService;
    }

    @Override
    public CommandResult execute() throws DatabaseException, ValidationException, BusinessLogicException {
        try {
            // Validate parameters first
            if (!validateParameters()) {
                return CommandResult.failure("Invalid parameters for user management");
            }

            // Execute based on action using service methods
            switch (action) {
                case VIEW_ALL:
                    List<User> allUsers = userService.findAllUsers();
                    return CommandResult.success("Found " + allUsers.size() + " users", allUsers);

                case GET_USER_DETAILS:
                    Optional<User> userOptional = userService.findUserById(targetUserId);
                    if (userOptional.isPresent()) {
                        return CommandResult.success("User details retrieved", userOptional.get());
                    } else {
                        throw new BusinessLogicException("User not found with ID: " + targetUserId);
                    }

                case DELETE_USER:
                    // Prevent admin from deleting themselves
                    if (targetUserId.equals(adminId)) {
                        throw new BusinessLogicException("Cannot delete your own account");
                    }

                    boolean deleted = userService.deleteUser(targetUserId);
                    if (deleted) {
                        return CommandResult.success("User deleted successfully");
                    } else {
                        throw new BusinessLogicException("Failed to delete user");
                    }

                default:
                    throw new BusinessLogicException("Unsupported management action: " + action);
            }

        } catch (ValidationException | BusinessLogicException e) {
            return CommandResult.failure("User management failed: " + e.getMessage(), e);
        } catch (Exception e) {
            return CommandResult.failure("Unexpected error during user management: " + e.getMessage(), e);
        }
    }

    @Override
    public String getDescription() {
        return action + " by admin ID " + adminId;
    }

    @Override
    public boolean validateParameters() throws ValidationException {
        if (userService == null) {
            throw new ValidationException("UserService is required", "UserService");
        }

        if (adminId == null || adminId <= 0) {
            throw new ValidationException("Valid admin ID is required", "AdminId", adminId);
        }

        // Validate parameters based on action
        switch (action) {
            case GET_USER_DETAILS:
            case DELETE_USER:
                if (targetUserId == null || targetUserId <= 0) {
                    throw new ValidationException("Valid target user ID is required", "TargetUserId", targetUserId);
                }
                break;
        }

        return true;
    }
}
