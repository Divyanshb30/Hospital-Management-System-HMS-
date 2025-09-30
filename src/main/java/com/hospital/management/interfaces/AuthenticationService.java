package com.hospital.management.interfaces;

import com.hospital.management.models.User;
import com.hospital.management.common.exceptions.AuthenticationException;

/**
 * Defines authentication operations for the system.
 */
public interface AuthenticationService {
    User login(String username, String password) throws AuthenticationException;
    void logout(String username) throws AuthenticationException;
    boolean isAuthenticated(String username);
    User getCurrentUser(String username);
}
