package com.theroom307.jcpm.core.controller.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(BadRequestException ex) {
        return createResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

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
                        .collect(Collectors.joining("\n")));
    }

    /**
     * Handle the MethodArgumentNotValidException which is thrown
     * when an input object's validation has failed. Override the
     * corresponding method from the parent class.
     */
    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                               @Nullable HttpHeaders headers,
                                                               @Nullable HttpStatusCode status,
                                                               @Nullable WebRequest request) {
        return createResponseEntity(HttpStatus.BAD_REQUEST,
                ex.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .collect(Collectors.joining("\n")));
    }

    /**
     * Handle the MethodArgumentTypeMismatchException which is thrown
     * when an input parameter has a wrong type, e.g., a string instead
     * of a number.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @SneakyThrows
    public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        return createResponseEntity(HttpStatus.BAD_REQUEST,
                String.format("'%s' must be a %s",
                        ex.getPropertyName(),
                        getRequiredTypeName(ex.getRequiredType())));
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

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<Object> handleItemNotFoundException(ItemNotFoundException exception) {
        return createResponseEntity(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnexpectedException(Exception e) {
        log.warn("Unexpected exception: " + e.getMessage());
        return createResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Sorry, something went wrong");
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(@Nullable HttpMessageNotReadableException ex,
                                                                  @Nullable HttpHeaders headers,
                                                                  @Nullable HttpStatusCode status,
                                                                  @Nullable WebRequest request) {
        return createResponseEntity(HttpStatus.BAD_REQUEST, "What was that?");
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                         @Nullable HttpHeaders headers,
                                                                         @Nullable HttpStatusCode status,
                                                                         @Nullable WebRequest request) {
        return createResponseEntity(HttpStatus.BAD_REQUEST, "%s method is not supported", ex.getMethod());
    }

    private ResponseEntity<Object> createResponseEntity(HttpStatus status, String message, String... args) {
        message = String.format(message, (Object[]) args);
        log.warn("Returning {} with the following error message: {}", status.value(), message);
        return new ResponseEntity<>(message, status);
    }

}
