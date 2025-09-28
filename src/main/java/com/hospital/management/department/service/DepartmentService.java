package com.hospital.management.department.service;

import com.hospital.management.department.model.Department;

import java.util.List;
import java.util.Optional;

/**
 * DepartmentService
 * -----------------
 * Business logic facade for Department operations.
 * Controllers call this; it delegates persistence to DepartmentDAO
 * and applies validations / domain rules.
 */
public interface DepartmentService {

    /** Create a new department and return the generated ID. */
    Long createDepartment(Department department);

    /** Fetch a department by primary key. */
    Optional<Department> getById(Long id);

    /** List all departments (consider pagination in impl). */
    List<Department> listAll();

    /** Case-insensitive search by name (e.g., "cardio"). */
    List<Department> searchByName(String nameQuery);

    /** List departments by type (e.g., CARDIOLOGY, NEUROLOGY). */
    List<Department> listByType(String type);

    /** List departments led by a specific head doctor. */
    List<Department> listByHeadDoctor(Long headDoctorId);

    /** Update all mutable fields of a department. */
    boolean updateDepartment(Department department);

    /** Update only capacity (quick adjustment). */
    boolean updateCapacity(Long id, int newCapacity);

    /** Delete department by ID. */
    boolean deleteDepartment(Long id);

    /** Total number of departments. */
    long count();
}
