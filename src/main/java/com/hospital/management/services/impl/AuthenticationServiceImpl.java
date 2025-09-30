package com.hospital.management.services.impl;

import com.hospital.management.dao.interfaces.UserDAO;
import com.hospital.management.dao.impl.UserDAOImpl;
import com.hospital.management.interfaces.AuthenticationService;
import com.hospital.management.models.User;
import com.hospital.management.common.utils.PasswordEncoder;
import com.hospital.management.common.exceptions.AuthenticationException;
import com.hospital.management.common.config.AppConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe singleton implementation of AuthenticationService.
 * Tracks authenticated users in memory.
 */
public class AuthenticationServiceImpl implements AuthenticationService {

    // Singleton with lazy-holder idiom (thread-safe, no sync overhead)
    private AuthenticationServiceImpl() {}

    private static class Holder {
        private static final AuthenticationServiceImpl INSTANCE = new AuthenticationServiceImpl();
    }
    public static AuthenticationServiceImpl getInstance() { return Holder.INSTANCE; }

    // Dependency - could be injected for DI setups
    private final UserDAO userDAO = new UserDAOImpl();

    // In-memory authenticated users & login attempts tracking
    private final Map<String, User> authenticatedUsers = new ConcurrentHashMap<>();
    private final Map<String, Integer> failedLoginAttempts = new ConcurrentHashMap<>();
    private final int maxAttempts = AppConfig.getInstance().getMaxLoginAttempts();

    @Override
    public User login(String username, String password) throws AuthenticationException {
        if (username == null || password == null) {
            throw new AuthenticationException("Username and password required.");
        }

        User user = userDAO.getAllUsers().stream()
                .filter(u -> username.equals(u.getUsername()))
                .findFirst()
                .orElse(null);

        if (user == null || !user.isActive()) {
            recordAttempt(username, false);
            throw new AuthenticationException("Invalid credentials or inactive user.", username);
        }

        if (!PasswordEncoder.verifyPassword(password, user.getPasswordHash())) {
            recordAttempt(username, false);
            if (failedLoginAttempts.getOrDefault(username, 0) >= maxAttempts) {
                user.setActive(false);
                userDAO.updateUser(user);
                throw new AuthenticationException("Account locked due to too many failed attempts.", username, "Lockout");
            }
            throw new AuthenticationException("Invalid credentials.", username);
        }
        recordAttempt(username, true);
        authenticatedUsers.put(username, user);
        return user;
    }

    @Override
    public void logout(String username) throws AuthenticationException {
        if (username == null || !authenticatedUsers.containsKey(username)) {
            throw new AuthenticationException("User not logged in.", username, "Logout");
        }
        authenticatedUsers.remove(username);
    }

    @Override
    public boolean isAuthenticated(String username) {
        return authenticatedUsers.containsKey(username);
    }

    @Override
    public User getCurrentUser(String username) {
        return authenticatedUsers.get(username);
    }

    private void recordAttempt(String username, boolean success) {
        if (success) {
            failedLoginAttempts.remove(username);
        } else {
            failedLoginAttempts.put(username, failedLoginAttempts.getOrDefault(username, 0) + 1);
        }
    }
}
