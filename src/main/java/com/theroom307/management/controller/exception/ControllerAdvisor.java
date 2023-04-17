package com.theroom307.management.controller.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException exception) {
        return createResponseEntity(HttpStatus.BAD_REQUEST,
                exception.getConstraintViolations()
                        .stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining(". "))
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @SneakyThrows
    public ResponseEntity<String> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException exception
    ) {
        var message = String.format("'%s' must be a %s",
                exception.getPropertyName(),
                getRequiredTypeName(exception.getRequiredType()));
        return createResponseEntity(HttpStatus.BAD_REQUEST, message);
    }

    private String getRequiredTypeName(Class<?> type) {
        if (type == null) {
            return "(unknown)";
        }
        String typeName = type.getName();
        if (typeName.equals("int") || typeName.equals("long")) {
            return "number";
        }
        return typeName;
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<String> handleProductNotFoundException(ProductNotFoundException exception) {
        return createResponseEntity(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleUnexpectedException() {
        return createResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Sorry, something went wrong");
    }

    private ResponseEntity<String> createResponseEntity(HttpStatus status, String message) {
        return new ResponseEntity<>(message, status);
    }

}
