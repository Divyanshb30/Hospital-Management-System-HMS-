package com.hospital.management.dao.interfaces;

import com.hospital.management.models.Department;
import java.util.List;

public interface DepartmentDAO {
    Department getDepartmentById(Long id);
    List<Department> getAllDepartments();
    List<Department> getActiveDepartments();
    boolean createDepartment(Department department);
    boolean updateDepartment(Department department);
    boolean deleteDepartment(Long id);
    Department getDepartmentByName(String name);
}
