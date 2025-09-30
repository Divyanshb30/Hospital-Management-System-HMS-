package com.hospital.management.services.impl;

import com.hospital.management.interfaces.DepartmentService;
import com.hospital.management.dao.interfaces.DepartmentDAO;
import com.hospital.management.dao.impl.DepartmentDAOImpl;
import com.hospital.management.models.Department;

import java.util.List;
import java.util.Optional;

public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentDAO departmentDAO = new DepartmentDAOImpl();

    @Override
    public List<Department> getAllActiveDepartments() {
        return departmentDAO.getActiveDepartments();
    }

    @Override
    public Optional<Department> findDepartmentById(Long departmentId) {
        if (departmentId == null) return Optional.empty();
        Department dept = departmentDAO.getDepartmentById(departmentId);
        return Optional.ofNullable(dept);
    }

    @Override
    public List<Department> getDepartmentsWithDoctors() {
        return departmentDAO.getDepartmentsWithDoctors();
    }
}
