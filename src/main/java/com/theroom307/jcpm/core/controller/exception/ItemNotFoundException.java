package com.theroom307.jcpm.core.controller.exception;

public abstract class ItemNotFoundException extends RuntimeException {
    ItemNotFoundException(String itemType, long itemId) {
        super(String.format("%s '%s' was not found", itemType, itemId));
    }
}
