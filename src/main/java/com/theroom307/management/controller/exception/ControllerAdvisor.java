package com.theroom307.management.controller.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    /**
     * Handle the ConstraintViolationException which is thrown
     * when an input parameter's validation has failed.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException exception) {
        return createResponseEntity(HttpStatus.BAD_REQUEST,
                exception.getConstraintViolations()
                        .stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining(". "))
        );
    }

    /**
     * Handle the MethodArgumentNotValidException which is thrown
     * when an input object's validation has failed. Override the
     * corresponding method from the parent class.
     */
    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return createResponseEntity(HttpStatus.BAD_REQUEST,
                ex.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .collect(Collectors.joining(", ")));
    }

    /**
     * Handle the MethodArgumentTypeMismatchException which is thrown
     * when an input parameter has a wrong type, e.g., a string instead
     * of a number.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @SneakyThrows
    public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(
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
    public ResponseEntity<Object> handleProductNotFoundException(ProductNotFoundException exception) {
        return createResponseEntity(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnexpectedException() {
        return createResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Sorry, something went wrong");
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return createResponseEntity(HttpStatus.BAD_REQUEST, "What was that?");
    }

    private ResponseEntity<Object> createResponseEntity(HttpStatus status, String message) {
        return new ResponseEntity<>(message, status);
    }

}
