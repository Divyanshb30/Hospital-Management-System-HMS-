package com.hospital.management.interfaces;

import com.hospital.management.models.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> findUserById(Long id);
    Optional<User> findUserByUsername(String username);
    List<User> findAllUsers();
    boolean registerUser(User user, String password);
    boolean updateUser(User user);
    boolean deleteUser(Long id);
    boolean authenticate(String username, String password);
}
