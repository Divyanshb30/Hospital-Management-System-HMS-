package com.hospital.management.department.service;

import com.hospital.management.department.dao.DepartmentDAO;
import com.hospital.management.department.model.Department;

import java.util.List;
import java.util.Optional;
import java.util.Objects;

/**
 * DepartmentServiceImpl
 * ---------------------
 * Implements DepartmentService using DepartmentDAO.
 * Applies validations and domain rules before delegating to DAO.
 */
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentDAO departmentDAO;

    public DepartmentServiceImpl(DepartmentDAO departmentDAO) {
        this.departmentDAO = Objects.requireNonNull(departmentDAO, "departmentDAO must not be null");
    }

    @Override
    public Long createDepartment(Department department) {
        validateDepartment(department);
        return departmentDAO.insert(department);
    }

    @Override
    public Optional<Department> getById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Department ID must be positive.");
        }
        return departmentDAO.findById(id);
    }

    @Override
    public List<Department> listAll() {
        return departmentDAO.findAll();
    }

    @Override
    public List<Department> searchByName(String nameQuery) {
        if (nameQuery == null || nameQuery.trim().isEmpty()) {
            throw new IllegalArgumentException("Search query must not be empty.");
        }
        return departmentDAO.searchByName(nameQuery);
    }

    @Override
    public List<Department> listByType(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Type must not be empty.");
        }
        return departmentDAO.findByType(type);
    }

    @Override
    public List<Department> listByHeadDoctor(Long headDoctorId) {
        if (headDoctorId == null || headDoctorId <= 0) {
            throw new IllegalArgumentException("Head doctor ID must be positive.");
        }
        return departmentDAO.findByHeadDoctorId(headDoctorId);
    }

    @Override
    public boolean updateDepartment(Department department) {
        validateDepartment(department);
        if (department.getId() == null || department.getId() <= 0) {
            throw new IllegalArgumentException("Department ID must be set for update.");
        }
        return departmentDAO.update(department);
    }

    @Override
    public boolean updateCapacity(Long id, int newCapacity) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Department ID must be positive.");
        }
        if (newCapacity < 0) {
            throw new IllegalArgumentException("Capacity must not be negative.");
        }
        return departmentDAO.updateCapacity(id, newCapacity);
    }

    @Override
    public boolean deleteDepartment(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Department ID must be positive.");
        }
        return departmentDAO.deleteById(id);
    }

    @Override
    public long count() {
        return departmentDAO.count();
    }

    // --- Internal validations ---
    private void validateDepartment(Department department) {
        if (department == null) {
            throw new IllegalArgumentException("Department must not be null.");
        }
        if (department.getName() == null || department.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Department name must not be empty.");
        }
        if (department.getCapacity() < 0) {
            throw new IllegalArgumentException("Capacity cannot be negative.");
        }
    }
}
