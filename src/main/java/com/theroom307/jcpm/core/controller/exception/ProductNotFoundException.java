package com.theroom307.jcpm.core.controller.exception;

public class ProductNotFoundException extends ItemNotFoundException {
    public ProductNotFoundException(long productId) {
        super("Product", productId);
    }
}
