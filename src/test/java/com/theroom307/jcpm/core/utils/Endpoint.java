package com.theroom307.jcpm.core.utils;

public enum Endpoint {
    PRODUCTS("/api/v1/product"),
    PRODUCT(PRODUCTS.getEndpoint() + "/%s"), // %s: product ID
    COMPONENTS("/api/v1/component"),
    COMPONENT(COMPONENTS.getEndpoint() + "/%s"); // %s: component ID

    private final String endpoint;

    Endpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }
}
