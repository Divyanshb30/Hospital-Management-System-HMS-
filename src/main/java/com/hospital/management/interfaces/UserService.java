package com.hospital.management.interfaces;

import com.hospital.management.models.User;
import com.hospital.management.common.enums.UserRole;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<User> findUserById(Long id);

    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByEmail(String email);  // ✅ ADD THIS

    List<User> findAllUsers();

    boolean registerUser(User user, String password);

    User createUser(String username, String password, String email, String phone, UserRole role);  // ✅ ADD THIS

    boolean updateUser(User user);

    boolean updateUserPassword(Long userId, String newPassword);  // ✅ ADD THIS

    boolean deleteUser(Long id);

    boolean authenticate(String username, String password);
    boolean verifyPassword(String username, String password);
    boolean updatePassword(Long userId, String newPassword);
}
