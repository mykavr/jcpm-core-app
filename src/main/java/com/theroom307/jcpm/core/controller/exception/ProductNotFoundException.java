package com.theroom307.jcpm.core.controller.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(long productId) {
        super(String.format("Product '%s' was not found", productId));
    }
}
