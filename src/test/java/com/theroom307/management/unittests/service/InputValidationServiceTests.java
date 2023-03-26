package com.theroom307.management.unittests.service;

import com.theroom307.management.controller.InputValidationService;
import com.theroom307.management.controller.exception.BadRequestException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InputValidationServiceTests {

    private final InputValidationService inputValidationService = new InputValidationService();

    @Test
    void pagination_ValidPageAndSizeTest() {
        inputValidationService.validatePaginationParams(0, 1);
    }

    @Test
    void pagination_InvalidPageTest() {
        var exception = assertThrows(BadRequestException.class, () ->
                inputValidationService.validatePaginationParams(-1, 1));

        assertThat(exception.getMessage())
                .as("Service should provide a proper error message")
                .isEqualTo("Page must not be negative");
    }

    @Test
    void pagination_InvalidPageSizeTest() {
        var exception = assertThrows(BadRequestException.class, () ->
                inputValidationService.validatePaginationParams(0, 0));

        assertThat(exception.getMessage())
                .as("Service should provide a proper error message")
                .isEqualTo("Page size must be greater than 0");
    }

}
