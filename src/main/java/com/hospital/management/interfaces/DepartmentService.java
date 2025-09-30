package com.hospital.management.interfaces;

import com.hospital.management.models.Department;
import java.util.List;
import java.util.Optional;

public interface DepartmentService {
    List<Department> getAllActiveDepartments();
    Optional<Department> findDepartmentById(Long departmentId);
    List<Department> getDepartmentsWithDoctors();
}
