package com.theroom307.jcpm.core.controller.exception;

public class ComponentNotFoundException extends ItemNotFoundException {

    public ComponentNotFoundException(long componentId) {
        super("Component", componentId);
    }

}
