package com.hospital.management.commands.AdminCommands;

import com.hospital.management.commands.Command;
import com.hospital.management.commands.CommandResult;
import com.hospital.management.common.exceptions.DatabaseException;
import com.hospital.management.common.exceptions.ValidationException;
import com.hospital.management.dao.interfaces.DepartmentDAO;
import com.hospital.management.dao.impl.DepartmentDAOImpl;
import com.hospital.management.models.Department;

import java.util.List;

public class ManageDepartmentsCommand implements Command {

    public enum DepartmentAction {
        VIEW_ALL, ADD_DEPARTMENT, DELETE_DEPARTMENT, GET_DEPARTMENT_DETAILS
    }

    private final Long adminId;
    private final DepartmentAction action;
    private final Department department;
    private final Long departmentId;
    private final DepartmentDAO departmentDAO;

    // Constructor for VIEW_ALL action
    public ManageDepartmentsCommand(Long adminId, DepartmentAction action) {
        this.adminId = adminId;
        this.action = action;
        this.department = null;
        this.departmentId = null;
        this.departmentDAO = new DepartmentDAOImpl();
    }

    // Constructor for ADD_DEPARTMENT action
    public ManageDepartmentsCommand(Long adminId, DepartmentAction action, Department department) {
        this.adminId = adminId;
        this.action = action;
        this.department = department;
        this.departmentId = null;
        this.departmentDAO = new DepartmentDAOImpl();
    }

    // Constructor for DELETE_DEPARTMENT or GET_DEPARTMENT_DETAILS action
    public ManageDepartmentsCommand(Long adminId, DepartmentAction action, Long departmentId) {
        this.adminId = adminId;
        this.action = action;
        this.department = null;
        this.departmentId = departmentId;
        this.departmentDAO = new DepartmentDAOImpl();
    }

    @Override
    public CommandResult execute() throws ValidationException, DatabaseException {
        if (!validateParameters()) {
            throw new ValidationException("Invalid parameters provided", "ManageDepartments");
        }

        try {
            switch (action) {
                case VIEW_ALL:
                    return viewAllDepartments();
                case ADD_DEPARTMENT:
                    return addDepartment();
                case DELETE_DEPARTMENT:
                    return deleteDepartment();
                case GET_DEPARTMENT_DETAILS:
                    return getDepartmentDetails();
                default:
                    throw new ValidationException("Invalid action specified", "Action");
            }
        } catch (Exception e) {
            throw new DatabaseException("Error managing departments: " + e.getMessage(), "DEPARTMENT_MANAGEMENT_ERROR");
        }
    }

    private CommandResult viewAllDepartments() {
        List<Department> departments = departmentDAO.getAllDepartments();
        return CommandResult.success("Departments retrieved successfully", departments);
    }

    private CommandResult addDepartment() throws ValidationException {
        if (department == null) {
            throw new ValidationException("Department data is required", "Department");
        }

        // Check if department with same name exists
        Department existingDept = departmentDAO.getDepartmentByName(department.getName());
        if (existingDept != null) {
            return CommandResult.failure("Department with name '" + department.getName() + "' already exists", null);
        }

        department.validate();
        boolean success = departmentDAO.createDepartment(department);

        if (success) {
            return CommandResult.success("Department created successfully", department);
        } else {
            return CommandResult.failure("Failed to create department", null);
        }
    }

    private CommandResult deleteDepartment() throws ValidationException {
        if (departmentId == null) {
            throw new ValidationException("Department ID is required", "DepartmentId");
        }

        // Check if department exists
        Department existingDept = departmentDAO.getDepartmentById(departmentId);
        if (existingDept == null) {
            return CommandResult.failure("Department not found", null);
        }

        boolean success = departmentDAO.deleteDepartment(departmentId);

        if (success) {
            return CommandResult.success("Department deleted successfully", existingDept);
        } else {
            return CommandResult.failure("Failed to delete department", null);
        }
    }

    private CommandResult getDepartmentDetails() throws ValidationException {
        if (departmentId == null) {
            throw new ValidationException("Department ID is required", "DepartmentId");
        }

        Department department = departmentDAO.getDepartmentById(departmentId);
        if (department == null) {
            return CommandResult.failure("Department not found", null);
        }

        return CommandResult.success("Department details retrieved successfully", department);
    }

    @Override
    public String getDescription() {
        return "Manage hospital departments - " + action.name();
    }

    @Override
    public boolean validateParameters() throws ValidationException {
        if (adminId == null || adminId <= 0) {
            throw new ValidationException("Valid admin ID is required", "AdminId");
        }

        if (action == null) {
            throw new ValidationException("Action is required", "Action");
        }

        return true;
    }
}
