package com.hospital.management.common.exceptions;

/**
 * Custom exception for business logic violations
 */
public class BusinessLogicException extends Exception {

    private final String businessRule;
    private final String entityType;
    private final Long entityId;

    public BusinessLogicException(String message) {
        super(message);
        this.businessRule = "Unknown";
        this.entityType = "Unknown";
        this.entityId = null;
    }

    public BusinessLogicException(String message, String businessRule) {
        super(message);
        this.businessRule = businessRule;
        this.entityType = "Unknown";
        this.entityId = null;
    }

    public BusinessLogicException(String message, String businessRule, String entityType) {
        super(message);
        this.businessRule = businessRule;
        this.entityType = entityType;
        this.entityId = null;
    }

    public BusinessLogicException(String message, String businessRule, String entityType, Long entityId) {
        super(message);
        this.businessRule = businessRule;
        this.entityType = entityType;
        this.entityId = entityId;
    }

    public BusinessLogicException(String message, Throwable cause) {
        super(message, cause);
        this.businessRule = "Unknown";
        this.entityType = "Unknown";
        this.entityId = null;
    }

    public String getBusinessRule() {
        return businessRule;
    }

    public String getEntityType() {
        return entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    @Override
    public String toString() {
        return String.format("BusinessLogicException [rule=%s, entity=%s, id=%s]: %s",
                businessRule, entityType, entityId, getMessage());
    }
}
