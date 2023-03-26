package com.theroom307.management.controller;

import com.theroom307.management.controller.exception.BadRequestException;
import org.springframework.stereotype.Service;

@Service
public class InputValidationService {
    public void validatePaginationParams(int page, int size) {
        if (page < 0) {
            throw new BadRequestException("Page must not be negative");
        }

        if (size < 1) {
            throw new BadRequestException("Page size must be greater than 0");
        }
    }
}
