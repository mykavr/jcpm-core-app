package com.theroom307.jcpm.core.utils;

public enum Item {
    PRODUCT("Product"),
    COMPONENT("Component");

    private final String name;

    Item(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
