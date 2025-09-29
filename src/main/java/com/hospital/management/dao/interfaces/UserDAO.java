package com.hospital.management.dao.interfaces;



import com.hospital.management.models.User;
import java.util.List;

public interface UserDAO {
    User getUserById(int id);
    List<User> getAllUsers();
    boolean createUser(User user);
    boolean updateUser(User user);
    boolean deleteUser(int id);
}
