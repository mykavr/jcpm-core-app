package com.theroom307.jcpm.core.utils.constant;

public enum Endpoint {

    PRODUCTS("/api/v1/product"),

    PRODUCT(PRODUCTS.getEndpoint() + "/%s"), // Path param: product ID

    PRODUCT_COMPONENTS(PRODUCT.getEndpoint() + "/components"), // Path param: product ID

    PRODUCT_COMPONENT(PRODUCT_COMPONENTS.getEndpoint() + "/%s"), // Path params: product ID, component ID

    COMPONENTS("/api/v1/component"),

    COMPONENT(COMPONENTS.getEndpoint() + "/%s"); // Path param: component ID

    private final String endpoint;

    Endpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getEndpoint(Object... pathParams) {
        return String.format(endpoint, pathParams);
    }
}
