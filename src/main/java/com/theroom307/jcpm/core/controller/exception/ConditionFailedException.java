package com.theroom307.jcpm.core.controller.exception;

public class ConditionFailedException extends RuntimeException {

    /**
     * This exception is thrown when an operation cannot be done because it
     * goes against the business rules. For example, when a product contains a
     * component, it is not possible to delete this component from the system.
     * @param message should describe the error and suggest the client how to
     *                resolve the business rule violation before re-sending the
     *                request.
     */
    public ConditionFailedException(String message) {
        super(message);
    }
}
