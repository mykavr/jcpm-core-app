package com.theroom307.management.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ProductNotFoundException extends ResponseStatusException {
    public ProductNotFoundException(String productId) {
        super(HttpStatus.NOT_FOUND, String.format("Product '%s' was not found", productId));
    }
}
