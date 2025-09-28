package com.hospital.management.department.dao;

import com.hospital.management.department.model.Department;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object (DAO) contract for Department entity.
 * Implement with JDBC (try-with-resources) in DepartmentDAOImpl.
 */
public interface DepartmentDAO {

    /** Insert a new department and return the generated ID. */
    Long insert(Department department);

    /** Find a department by primary key. */
    Optional<Department> findById(Long id);

    /** Return all departments (consider pagination in impl if data is large). */
    List<Department> findAll();

    /** Case-insensitive search by name (e.g., "cardio"). */
    List<Department> searchByName(String nameQuery);

    /** Find departments by type (e.g., CARDIOLOGY, NEUROLOGY). */
    List<Department> findByType(String type);

    /** Find departments led by a specific head doctor. */
    List<Department> findByHeadDoctorId(Long headDoctorId);

    /** Update all mutable fields of a department (by id). Returns true if updated. */
    boolean update(Department department);

    /** Update only capacity (handy for quick adjustments). */
    boolean updateCapacity(Long id, int newCapacity);

    /** Delete by id. Returns true if a row was deleted. */
    boolean deleteById(Long id);

    /** Count total departments. */
    long count();
}
