package com.hospital.management.commands;

import com.hospital.management.common.exceptions.DatabaseException;
import com.hospital.management.common.exceptions.ValidationException;
import com.hospital.management.common.exceptions.BusinessLogicException;

/**
 * Command interface implementing the Command Pattern
 * All user actions are encapsulated as commands
 */
public interface Command {

    /**
     * Execute the command
     * @return CommandResult containing success/failure and any return data
     * @throws Exception for any errors during execution
     */
    CommandResult execute() throws DatabaseException, ValidationException, BusinessLogicException;

    /**
     * Get command description for logging/debugging
     * @return String description of the command
     */
    String getDescription();

    /**
     * Validate command parameters before execution
     * @return true if parameters are valid
     * @throws ValidationException if validation fails
     */
    boolean validateParameters() throws ValidationException;
}
