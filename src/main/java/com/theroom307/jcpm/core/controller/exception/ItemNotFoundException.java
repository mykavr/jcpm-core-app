package com.theroom307.jcpm.core.controller.exception;

public class ItemNotFoundException extends NotFoundException {
    public ItemNotFoundException(String itemType, long itemId) {
        super(String.format("%s '%s' was not found", itemType, itemId));
    }
}
