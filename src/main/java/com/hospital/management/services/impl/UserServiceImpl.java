package com.hospital.management.services.impl;

import com.hospital.management.interfaces.UserService;
import com.hospital.management.dao.interfaces.UserDAO;
import com.hospital.management.dao.impl.UserDAOImpl;
import com.hospital.management.models.User;
import com.hospital.management.common.utils.PasswordEncoder;
import com.hospital.management.common.exceptions.ValidationException;

import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {

    private final UserDAO userDAO = new UserDAOImpl();

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
            // Validate user fields (excluding passwordHash, will be set here)
            user.validate();

            // Encode password with salt
            String encodedPassword = PasswordEncoder.encodePassword(password);
            user.setPasswordHash(encodedPassword);

            return userDAO.createUser(user);

        } catch (ValidationException e) {
            // Log or rethrow as needed
            System.err.println("User validation failed during registration: " + e.getMessage());
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
            return userDAO.updateUser(user);

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
        return userDAO.deleteUser(id.intValue());
    }

    @Override
    public boolean authenticate(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        Optional<User> userOpt = findUserByUsername(username);

        if (userOpt.isEmpty()) return false;
        User user = userOpt.get();

        if (!user.isActive()) return false;

        return PasswordEncoder.verifyPassword(password, user.getPasswordHash());
    }
}
